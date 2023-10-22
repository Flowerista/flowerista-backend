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
    id integer NOT NULL DEFAULT nextval('bouquete_id_seq'::regclass),
    defaultprice integer NOT NULL,
    discount integer,
    discountprice integer,
    itemcode character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    quantity integer,
    size character varying(255) COLLATE pg_catalog."default",
    soldquantity integer,
    CONSTRAINT bouquete_pkey PRIMARY KEY (id),
    CONSTRAINT uk_koe7o3qi2bawao29w4s2g04vh UNIQUE (itemcode),
    CONSTRAINT uk_o9reytbtg4x0o1hyv82iek4ra UNIQUE (name),
    CONSTRAINT bouquete_size_check CHECK (size::text = ANY (ARRAY['SMALL'::character varying, 'MEDIUM'::character varying, 'LARGE'::character varying]::text[]))
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

INSERT INTO public.bouquete (defaultprice, discount, discountprice, itemcode, name, quantity, size, soldquantity) VALUES
(50, 10, 45, 'BQ001', 'Spring Bouquet', 20, NULL, 5),
(40, NULL, NULL, 'BQ002', 'Summer Bouquet', 15, 'SMALL', 2),
(60, 15, 51, 'BQ003', 'Autumn Bouquet', 25, 'MEDIUM', 8);

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
