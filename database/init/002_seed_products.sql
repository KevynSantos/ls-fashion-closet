INSERT INTO product (
    name,
    description,
    price,
    stock,
    category,
    size,
    image_url,
    active,
    created_at
)
SELECT
    'Vestido Rose Glam',
    'Peca leve com acabamento delicado para eventos e encontros especiais.',
    159.90,
    8,
    'Vestidos',
    'P/M/G',
    '/placeholder.svg',
    1,
    NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Vestido Rose Glam');

INSERT INTO product (
    name,
    description,
    price,
    stock,
    category,
    size,
    image_url,
    active,
    created_at
)
SELECT
    'Blusa Cetim Perola',
    'Toque acetinado, caimento elegante e brilho sutil para compor looks versateis.',
    89.90,
    12,
    'Blusas',
    'P/M/G',
    '/placeholder.svg',
    1,
    NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Blusa Cetim Perola');

INSERT INTO product (
    name,
    description,
    price,
    stock,
    category,
    size,
    image_url,
    active,
    created_at
)
SELECT
    'Conjunto Pink Soft',
    'Conjunto confortavel com visual feminino e moderno para o dia a dia.',
    189.90,
    5,
    'Conjuntos',
    'P/M/G',
    '/placeholder.svg',
    1,
    NOW(6)
WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Conjunto Pink Soft');
