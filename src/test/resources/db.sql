CREATE SEQUENCE IF NOT EXISTS public.bouquete_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.color_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public.flower_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

CREATE TABLE IF NOT EXISTS public.bouquete
(
    id SERIAL PRIMARY KEY,
    itemcode VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    imageUrls JSONB,
    quantity INTEGER,
    soldquantity INTEGER
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_koe7o3qi2bawao29w4s2g04vh ON public.bouquete (itemcode);
CREATE UNIQUE INDEX IF NOT EXISTS uk_o9reytbtg4x0o1hyv82iek4ra ON public.bouquete (name);

CREATE TABLE IF NOT EXISTS public.bouquete_size
(
    id SERIAL PRIMARY KEY,
    bouquete_id INTEGER REFERENCES public.bouquete(id) ON DELETE CASCADE,
    size VARCHAR(255) NOT NULL CHECK (size IN ('SMALL', 'MEDIUM', 'LARGE')),
    defaultprice NUMERIC NOT NULL,
    discount NUMERIC,
    discountprice NUMERIC,
    is_sale BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_bouquete_size_bouquete_id_size UNIQUE (bouquete_id, size)
);

CREATE TABLE IF NOT EXISTS public.color
(
    id integer NOT NULL DEFAULT nextval('color_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT color_pkey PRIMARY KEY (id),
    CONSTRAINT uk_n3axgangk6yuxhrb2o7fk9oa7 UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.flower
(
    id integer NOT NULL DEFAULT nextval('flower_id_seq'::regclass),
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT flower_pkey PRIMARY KEY (id),
    CONSTRAINT uk_j31rn9xswibo1xak17l54ftdt UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.bouquete_color
(
    bouquete_id integer NOT NULL,
    color_id integer NOT NULL,
    CONSTRAINT bouquete_color_pkey PRIMARY KEY (bouquete_id, color_id),
    CONSTRAINT fk4bnd6fpk9t33s78n4doiqe60s FOREIGN KEY (color_id)
        REFERENCES public.color (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkpbxo7lgdss3s3ndypoyhhcid2 FOREIGN KEY (bouquete_id)
        REFERENCES public.bouquete (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.bouquete_flower
(
    bouquete_id integer NOT NULL,
    flower_id integer NOT NULL,
    CONSTRAINT bouquete_flower_pkey PRIMARY KEY (bouquete_id, flower_id),
    CONSTRAINT fkg56tt3cvnepsgnslife1vvtkt FOREIGN KEY (bouquete_id)
        REFERENCES public.bouquete (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkgp0o370t9kmxtl96ky4c91cua FOREIGN KEY (flower_id)
        REFERENCES public.flower (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);


INSERT INTO color(name) VALUES ('DaoTest1');
INSERT INTO color(name) VALUES ('DaoTest2');
INSERT INTO color(name) VALUES ('DaoTest3');
INSERT INTO color(name) VALUES ('DaoTest4');

INSERT INTO flower(name) VALUES ('DaoTest1');
INSERT INTO flower(name) VALUES ('DaoTest2');
INSERT INTO flower(name) VALUES ('DaoTest3');
INSERT INTO flower(name) VALUES ('DaoTest4');

INSERT INTO public.bouquete (itemcode, name, quantity, soldquantity) VALUES
('BQ001', 'Spring Bouquet', 20, 5),
('BQ002', 'Summer Bouquet', 15, 2),
('BQ003', 'Autumn Bouquet', 25, 8);


INSERT INTO public.bouquete_size (bouquete_id, defaultprice, discount, discountprice, size) VALUES
(1, 50, 10, 40, 'SMALL'),
(1, 60, 10, 50, 'MEDIUM'),
(2, 40, NULL, NULL, 'SMALL'),
(3, 60, 15, 45, 'MEDIUM');

INSERT INTO public.bouquete_color (bouquete_id, color_id) VALUES
(1, 1),
(1, 2),
(2, 3),
(3, 1),
(3, 2),
(3, 3);

INSERT INTO public.bouquete_flower (bouquete_id, flower_id) VALUES
(1, 1),
(1, 2),
(2, 2),
(3, 1),
(3, 3);
