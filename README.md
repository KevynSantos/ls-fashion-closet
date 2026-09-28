# LS Fashion Closet

Loja virtual responsiva com React, Java Spring Boot, Hibernate/JPA, MySQL, phpMyAdmin e Checkout Pro do Mercado Pago.

## Rodando localmente

1. Suba MySQL e phpMyAdmin:

```bash
docker compose up -d
```

2. Abra o backend:

```bash
mvn spring-boot:run
```

3. Abra o frontend:

```bash
cd frontend
npm install
npm run dev
```

4. Acesse:

- Loja: `http://localhost:5173`
- API: `http://localhost:8080/api`
- phpMyAdmin: `http://localhost:8081`

## Acessos locais

- MySQL host: `localhost`
- Banco: `ls_fashion_closet`
- Usuario: `ls_user`
- Senha: `ls_password`
- Admin da loja: `admin` / `admin123`

Troque o usuario e senha do admin em `src/main/resources/application.properties`.

## Mercado Pago

As credenciais ficam em:

```properties
mercadopago.access-token=COLOQUE_SEU_ACCESS_TOKEN_AQUI
mercadopago.public-key=COLOQUE_SUA_PUBLIC_KEY_AQUI
mercadopago.statement-descriptor=LS CLOSET
mercadopago.notification-url=
```

Para receber webhook localmente, exponha o backend com uma URL HTTPS publica, por exemplo via ngrok, e preencha:

```properties
mercadopago.notification-url=https://sua-url-publica/api/payments/webhook
```

## Como pegar as credenciais no Mercado Pago

1. Acesse [Mercado Pago Developers](https://www.mercadopago.com.br/developers/pt).
2. Clique em `Entrar` no canto superior direito e use a conta Mercado Pago da loja.
3. Entre em `Suas integracoes` ou `Minhas aplicacoes`.
4. Crie uma aplicacao para a loja, caso ainda nao exista.
5. Abra a aplicacao e entre na area `Credenciais`.
6. Para testes, copie `Public Key` e `Access Token` de teste.
7. Para vender de verdade, copie `Public Key` e `Access Token` de producao.
8. Cole o `Access Token` em `mercadopago.access-token`.
9. Cole a `Public Key` em `mercadopago.public-key`.
10. Reinicie o backend depois de trocar as credenciais.

O `Access Token` e privado: nao coloque no React, nao envie para clientes e nao publique no GitHub.
