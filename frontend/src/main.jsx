import React, { useEffect, useMemo, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  BadgeCheck,
  Heart,
  Lock,
  Minus,
  Package,
  Plus,
  ShoppingBag,
  Trash2,
  Truck,
  X
} from 'lucide-react';
import './styles.css';

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api';
const money = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

function authHeader(credentials) {
  if (!credentials.username || !credentials.password) return {};
  return { Authorization: `Basic ${btoa(`${credentials.username}:${credentials.password}`)}` };
}

async function api(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers ?? {}) },
    ...options
  });
  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    throw new Error(body.message ?? 'Nao foi possivel concluir a acao.');
  }
  if (response.status === 204) return null;
  return response.json();
}

function App() {
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState([]);
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [adminOpen, setAdminOpen] = useState(false);
  const [credentials, setCredentials] = useState({ username: 'admin', password: 'admin123' });
  const [orders, setOrders] = useState([]);
  const [adminProducts, setAdminProducts] = useState([]);
  const [message, setMessage] = useState('');

  const cartTotal = useMemo(
    () => cart.reduce((total, item) => total + item.product.price * item.quantity, 0),
    [cart]
  );

  useEffect(() => {
    loadCatalog();
  }, []);

  async function loadCatalog() {
    setProducts(await api('/products'));
  }

  async function loadAdmin() {
    const headers = authHeader(credentials);
    const [loadedProducts, loadedOrders] = await Promise.all([
      api('/admin/products', { headers }),
      api('/admin/orders', { headers })
    ]);
    setAdminProducts(loadedProducts);
    setOrders(loadedOrders);
  }

  function addToCart(product) {
    setCart((current) => {
      const existing = current.find((item) => item.product.id === product.id);
      if (existing) {
        return current.map((item) =>
          item.product.id === product.id ? { ...item, quantity: item.quantity + 1 } : item
        );
      }
      return [...current, { product, quantity: 1 }];
    });
    setDrawerOpen(true);
  }

  function changeQuantity(productId, delta) {
    setCart((current) =>
      current
        .map((item) =>
          item.product.id === productId
            ? { ...item, quantity: Math.max(1, item.quantity + delta) }
            : item
        )
        .filter((item) => item.quantity > 0)
    );
  }

  async function checkout(event) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const payload = {
      customerName: form.get('customerName'),
      customerEmail: form.get('customerEmail'),
      customerPhone: form.get('customerPhone'),
      customerDocument: form.get('customerDocument'),
      shippingZipCode: form.get('shippingZipCode'),
      shippingStreet: form.get('shippingStreet'),
      shippingNumber: form.get('shippingNumber'),
      shippingComplement: form.get('shippingComplement'),
      shippingNeighborhood: form.get('shippingNeighborhood'),
      shippingCity: form.get('shippingCity'),
      shippingState: form.get('shippingState'),
      items: cart.map((item) => ({ productId: item.product.id, quantity: item.quantity }))
    };
    const response = await api('/checkout', { method: 'POST', body: JSON.stringify(payload) });
    window.location.href = response.checkoutUrl;
  }

  async function saveProduct(event) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const id = form.get('id');
    const payload = {
      name: form.get('name'),
      description: form.get('description'),
      price: Number(form.get('price')),
      stock: Number(form.get('stock')),
      category: form.get('category'),
      size: form.get('size'),
      imageUrl: form.get('imageUrl'),
      active: form.get('active') === 'on'
    };
    const headers = authHeader(credentials);
    await api(id ? `/admin/products/${id}` : '/admin/products', {
      method: id ? 'PUT' : 'POST',
      headers,
      body: JSON.stringify(payload)
    });
    event.currentTarget.reset();
    setMessage('Produto salvo com sucesso.');
    await loadCatalog();
    await loadAdmin();
  }

  async function deleteProduct(id) {
    await api(`/admin/products/${id}`, { method: 'DELETE', headers: authHeader(credentials) });
    await loadCatalog();
    await loadAdmin();
  }

  async function updateOrderStatus(id, status) {
    await api(`/admin/orders/${id}/status`, {
      method: 'PATCH',
      headers: authHeader(credentials),
      body: JSON.stringify({ status })
    });
    await loadAdmin();
  }

  return (
    <>
      <header className="topbar">
        <a className="brand" href="#catalog">
          <span className="brand-mark">LS</span>
          <span>
            <strong>LS Closet</strong>
            <small>o glamour que seu closet merece</small>
          </span>
        </a>
        <nav>
          <a href="#catalog">Vitrine</a>
          <a href="#shipping">Envios</a>
          <button className="icon-button" onClick={() => setAdminOpen(true)} aria-label="Abrir admin">
            <Lock size={19} />
          </button>
          <button className="cart-button" onClick={() => setDrawerOpen(true)}>
            <ShoppingBag size={19} />
            <span>{cart.reduce((total, item) => total + item.quantity, 0)}</span>
          </button>
        </nav>
      </header>

      <main>
        <section className="hero">
          <div className="hero-copy">
            <span className="eyebrow">Loja online</span>
            <h1>LS Fashion Closet</h1>
            <p>Looks femininos, delicados e prontos para deixar seu dia com um toque de glamour.</p>
            <a className="primary-link" href="#catalog">Ver pecas</a>
          </div>
          <div className="hero-panel">
            <Heart />
            <strong>Curadoria boutique</strong>
            <span>Rosa, dourado, brilho sutil e atendimento feito com carinho.</span>
          </div>
        </section>

        <section className="trust-strip" id="shipping">
          <span><BadgeCheck size={18} /> Compra via Mercado Pago</span>
          <span><Truck size={18} /> Enviamos para todo o Brasil</span>
          <span><Package size={18} /> Estoque controlado pelo painel</span>
        </section>

        <section className="section-heading" id="catalog">
          <span>Vitrine</span>
          <h2>Escolha seu novo favorito</h2>
        </section>

        <section className="product-grid">
          {products.map((product) => (
            <article className="product-card" key={product.id}>
              <img src={product.imageUrl || '/placeholder.svg'} alt={product.name} />
              <div>
                <span>{product.category || 'Novidade'}</span>
                <h3>{product.name}</h3>
                <p>{product.description}</p>
                <footer>
                  <strong>{money.format(product.price)}</strong>
                  <button onClick={() => addToCart(product)}>Adicionar</button>
                </footer>
              </div>
            </article>
          ))}
        </section>
      </main>

      {drawerOpen && (
        <aside className="drawer">
          <div className="drawer-panel">
            <header>
              <h2>Sacola</h2>
              <button className="icon-button" onClick={() => setDrawerOpen(false)} aria-label="Fechar sacola">
                <X />
              </button>
            </header>

            {cart.length === 0 ? (
              <p className="empty">Sua sacola ainda esta vazia.</p>
            ) : (
              <>
                <div className="cart-list">
                  {cart.map((item) => (
                    <div className="cart-item" key={item.product.id}>
                      <img src={item.product.imageUrl || '/placeholder.svg'} alt={item.product.name} />
                      <div>
                        <strong>{item.product.name}</strong>
                        <span>{money.format(item.product.price)}</span>
                        <div className="quantity">
                          <button onClick={() => changeQuantity(item.product.id, -1)}><Minus size={14} /></button>
                          <b>{item.quantity}</b>
                          <button onClick={() => changeQuantity(item.product.id, 1)}><Plus size={14} /></button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>

                <form className="checkout-form" onSubmit={checkout}>
                  <strong>Total: {money.format(cartTotal)}</strong>
                  <input name="customerName" placeholder="Nome completo" required />
                  <input name="customerEmail" type="email" placeholder="E-mail" required />
                  <input name="customerPhone" placeholder="WhatsApp com DDD" required />
                  <input name="customerDocument" placeholder="CPF" required />
                  <input name="shippingZipCode" placeholder="CEP" required />
                  <input name="shippingStreet" placeholder="Rua / Avenida" required />
                  <div className="form-grid">
                    <input name="shippingNumber" placeholder="Numero" required />
                    <input name="shippingComplement" placeholder="Complemento" />
                  </div>
                  <input name="shippingNeighborhood" placeholder="Bairro" required />
                  <div className="form-grid">
                    <input name="shippingCity" placeholder="Cidade" required />
                    <input name="shippingState" placeholder="UF" maxLength="2" required />
                  </div>
                  <button className="primary-button">Pagar com Mercado Pago</button>
                </form>
              </>
            )}
          </div>
        </aside>
      )}

      {adminOpen && (
        <aside className="drawer">
          <div className="drawer-panel admin-panel">
            <header>
              <h2>Painel administrativo</h2>
              <button className="icon-button" onClick={() => setAdminOpen(false)} aria-label="Fechar admin">
                <X />
              </button>
            </header>

            <div className="admin-login">
              <input value={credentials.username} onChange={(event) => setCredentials({ ...credentials, username: event.target.value })} />
              <input type="password" value={credentials.password} onChange={(event) => setCredentials({ ...credentials, password: event.target.value })} />
              <button onClick={loadAdmin}>Entrar</button>
            </div>

            <form className="product-form" onSubmit={saveProduct}>
              <input name="id" placeholder="ID para editar, deixe vazio para novo" />
              <input name="name" placeholder="Nome da peca" required />
              <input name="price" type="number" step="0.01" placeholder="Preco" required />
              <input name="stock" type="number" placeholder="Estoque" required />
              <input name="category" placeholder="Categoria" />
              <input name="size" placeholder="Tamanho" />
              <input name="imageUrl" placeholder="URL da imagem" />
              <textarea name="description" placeholder="Descricao" />
              <label><input name="active" type="checkbox" defaultChecked /> Ativo na vitrine</label>
              <button className="primary-button">Salvar produto</button>
              {message && <span className="success">{message}</span>}
            </form>

            <div className="admin-list">
              <h3>Produtos</h3>
              {adminProducts.map((product) => (
                <div className="admin-row" key={product.id}>
                  <span>#{product.id} {product.name} - {money.format(product.price)}</span>
                  <button onClick={() => deleteProduct(product.id)}><Trash2 size={16} /></button>
                </div>
              ))}
            </div>

            <div className="admin-list">
              <h3>Vendas</h3>
              {orders.map((order) => (
                <div className="admin-row" key={order.id}>
                  <span>#{order.id} {order.customerName} - {order.customerPhone} - {money.format(order.total)} - {order.status}</span>
                  <select value={order.status} onChange={(event) => updateOrderStatus(order.id, event.target.value)}>
                    {['PENDING', 'WAITING_PAYMENT', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELED'].map((status) => (
                      <option key={status}>{status}</option>
                    ))}
                  </select>
                </div>
              ))}
            </div>
          </div>
        </aside>
      )}
    </>
  );
}

createRoot(document.getElementById('root')).render(<App />);
