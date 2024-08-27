--
-- PostgreSQL database dump
--

-- Dumped from database version 13.3 (Debian 13.3-1.pgdg100+1)
-- Dumped by pg_dump version 13.4

-- Started on 2021-09-27 09:05:57

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

DROP DATABASE "demoapp-timezone";
--
-- TOC entry 2954 (class 1262 OID 16385)
-- Name: demoapp-timezone; Type: DATABASE; Schema: -; Owner: demoapp
--

CREATE DATABASE "demoapp-timezone" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.utf8';


ALTER DATABASE "demoapp-timezone" OWNER TO demoapp;

\connect -reuse-previous=on "dbname='demoapp-timezone'"

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 2955 (class 0 OID 0)
-- Dependencies: 3
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 201 (class 1259 OID 16400)
-- Name: authorities; Type: TABLE; Schema: public; Owner: demoapp
--

CREATE TABLE public.authorities (
    username text NOT NULL,
    authority text NOT NULL
);


ALTER TABLE public.authorities OWNER TO demoapp;

--
-- TOC entry 202 (class 1259 OID 16427)
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: demoapp
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO demoapp;

--
-- TOC entry 203 (class 1259 OID 16429)
-- Name: timezone; Type: TABLE; Schema: public; Owner: demoapp
--

CREATE TABLE public.timezone (
    id bigint NOT NULL,
    city character varying(255),
    gmt integer NOT NULL,
    name character varying(255),
    username character varying(255)
);


ALTER TABLE public.timezone OWNER TO demoapp;

--
-- TOC entry 200 (class 1259 OID 16386)
-- Name: users; Type: TABLE; Schema: public; Owner: demoapp
--

CREATE TABLE public.users (
    username text NOT NULL,
    password text NOT NULL,
    enabled boolean NOT NULL
);


ALTER TABLE public.users OWNER TO demoapp;

--
-- TOC entry 2817 (class 2606 OID 16436)
-- Name: timezone timezone_pkey; Type: CONSTRAINT; Schema: public; Owner: demoapp
--

ALTER TABLE ONLY public.timezone
    ADD CONSTRAINT timezone_pkey PRIMARY KEY (id);


--
-- TOC entry 2815 (class 2606 OID 16393)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: demoapp
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (username);


--
-- TOC entry 2818 (class 2606 OID 16406)
-- Name: authorities username_constraint; Type: FK CONSTRAINT; Schema: public; Owner: demoapp
--

ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT username_constraint FOREIGN KEY (username) REFERENCES public.users(username);


-- Completed on 2021-09-27 09:05:57

--
-- PostgreSQL database dump complete
--

