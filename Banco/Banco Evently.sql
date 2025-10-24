CREATE DATABASE EVENTLY;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pg_trgm; -- buscas textuais masi rapidas

--Organizadores
CREATE TABLE organizadores (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(), --UUID é o tipo de dados, e o uuid_generate_v4 é uma função da extensão uuid-ossp que gera numeros aleatorios
  nome TEXT NOT NULL,
  descricao TEXT,
  email_contato TEXT,
  telefone_contato TEXT,
  site TEXT,
  --Isso é para ter controle de historico de registros. 
  criado_em timestamptz DEFAULT now(),
  atualizado_em timestamptz,
);

--Locais
CREATE TABLE locais (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  nome TEXT NOT NULL,
  descricao TEXT,
  endereco TEXT,
  cidade TEXT,
  estado TEXT,
  pais TEXT,
  capacidade integer,
  criado_em timestamptz DEFAULT now(),
  atualizado_em timestamptz,
);

--Categorias
CREATE TABLE categorias (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  nome TEXT NOT NULL UNIQUE,
  slug TEXT NOT NULL UNIQUE
);

--Eventos(FIXO, COMO ROCK IN RIO)
CREATE TABLE eventos (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  -- se organizador e local for excluído, outro tem que assumir antes
  id_organizador UUID REFERENCES organizadores(id) NOT NULL,
  id_local UUID REFERENCES locais(id) NOT NULL,
  id_categoria UUID REFERENCES categorias(id),
  titulo TEXT NOT NULL,
  slug TEXT NOT NULL UNIQUE,
  descricao_curta TEXT,
  descricao TEXT,
  status TEXT NOT NULL DEFAULT 'rascunho', -- rascunho|publicado|cancelado|arquivado
  capacidade integer,
  metadados JSONB,
  criado_por UUID,
  criado_em timestamptz DEFAULT now(),
  atualizado_em timestamptz,
);

--Programacao eventos(DE TEMPO EM TEMPO ROCK IN RIO LONDRES AS 15H, DAI PUXA O EVENTO)
CREATE TABLE programacoes_evento (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  id_evento UUID REFERENCES eventos(id) ON DELETE CASCADE,
  inicio_em timestamptz NOT NULL,
  fim_em timestamptz,
  titulo TEXT NOT NULL,
  descricao TEXT,
  capacidade integer,
  criado_em timestamptz DEFAULT now(),
  atualizado_em timestamptz
);

-- ETIQUETAS (tags)
CREATE TABLE etiquetas (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  nome TEXT NOT NULL UNIQUE
);

-- RELAÇÃO EVENTO x ETIQUETA (muitos-para-muitos)
CREATE TABLE evento_etiquetas (
  id_evento UUID REFERENCES eventos(id) ON DELETE CASCADE,
  id_etiqueta UUID REFERENCES etiquetas(id) ON DELETE CASCADE,
  PRIMARY KEY (id_evento, id_etiqueta)
);

-- USUÁRIOS
CREATE TABLE usuarios (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email TEXT UNIQUE NOT NULL,
  nome TEXT,
  senha_hash TEXT,
  papel TEXT DEFAULT 'usuario', -- usuario|organizador|admin
  criado_em timestamptz DEFAULT now()
);

-- INGRESSOS
CREATE TABLE ingressos (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  id_evento UUID REFERENCES eventos(id) ON DELETE CASCADE,
  nome TEXT NOT NULL,
  --preco numeric(10,2) DEFAULT 0,
  --quantidade integer,
  --vendas_inicio timestamptz,
  --vendas_fim timestamptz,
  criado_em timestamptz DEFAULT now()
);

-- PARTICIPAÇÕES (inscrições/compras)
CREATE TABLE participacoes (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  id_usuario UUID REFERENCES usuarios(id) ON DELETE SET NULL,
  id_ingresso UUID REFERENCES ingressos(id) ON DELETE SET NULL,
  id_evento UUID REFERENCES eventos(id) ON DELETE CASCADE,
  criado_em timestamptz DEFAULT now()
);

-- COMENTÁRIOS / AVALIAÇÕES
CREATE TABLE comentarios (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  id_evento UUID REFERENCES eventos(id) ON DELETE CASCADE,
  id_usuario UUID REFERENCES usuarios(id) ON DELETE SET NULL,
  id_comentario_pai UUID REFERENCES comentarios(id) ON DELETE CASCADE,
  conteudo TEXT NOT NULL,
  avaliacao smallint,
  criado_em timestamptz DEFAULT now(),
);

-- IMAGENS DO EVENTO
CREATE TABLE imagens_evento (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  id_evento UUID REFERENCES eventos(id) ON DELETE CASCADE,
  url TEXT NOT NULL,
  descricao_alternativa TEXT,
  capa boolean DEFAULT false,
  criado_em timestamptz DEFAULT now()
);

--FAVORITOS
CREATE TABLE favoritos (
  id_usuario UUID REFERENCES usuarios(id) ON DELETE CASCADE, -- se o usuário for excluído, apaga os favoritos dele
  id_evento UUID REFERENCES eventos(id) ON DELETE CASCADE,   -- se o evento for excluído, remove dos favoritos
  -- criado_em timestamptz DEFAULT now(), -- data em que foi favoritado
  -- tirado_em timestamptz 			   -- data que foi desfavoritado | a fins de controle, se nao curtir tirar. 
  PRIMARY KEY (id_usuario, id_evento)
);



