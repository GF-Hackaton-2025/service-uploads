# ☁️ service-uploads 

O `service-uploads` é um microsserviço em Java 21 com Spring WebFlux responsável por:

- ✅ Verificar o token de autenticação (JWT)
- ✅ Receber arquivos via API REST
- ✅ Enviar os arquivos para um bucket S3 na AWS
- ✅ Publicar mensagens no SQS (sucesso ou erro)

Esse serviço faz parte da arquitetura de microsserviços do projeto **FIAP X - Sistema de Processamento de Vídeos**.

---

## 🧰 Tecnologias Utilizadas

- Java 21
- Spring WebFlux
- Spring Security (JWT)
- Amazon S3 (upload)
- Amazon SQS (mensageria)
- Docker + Kubernetes (K8s)
- Terraform (infraestrutura como código)
- Swagger (documentação REST)
- Lombok
- Testes com JUnit + Mockito

---

## 🚀 Funcionalidades

- 🔐 Validação do JWT enviado no header
- 📤 Upload de arquivos via multipart/form-data
- ☁️ Armazena os arquivos em bucket S3
- 📩 Envia mensagens (sucesso/erro) para o SQS
- ✅ Validação de arquivos e tratamento de exceções
- 🧪 Testes unitários cobrindo serviços e controladores

---

## 📦 Como executar localmente

### Pré-requisitos

- Java 21
- Maven 3.8+
- Docker (opcional para execução em container)

### 📁 Documentação da API

- Após a aplicação estar em execução, a documentação estará disponível em:

```
http://<url_service>/swagger-ui/index.html
```

### 🐳 Docker

```
docker build -t service-uploads .
```

## 📝 Considerações Finais

O ```service-uploads``` foi projetado com foco em escalabilidade, desempenho e boas práticas de engenharia de software moderna. Ao utilizar uma arquitetura reativa com Spring WebFlux, mensageria assíncrona (SQS) e armazenamento em nuvem (S3), o serviço é capaz de lidar com múltiplos uploads simultâneos com eficiência e resiliência.

Além disso, com o uso de Docker, Kubernetes e Terraform, o serviço está preparado para ser implantado de forma automatizada e consistente em diferentes ambientes, garantindo facilidade de manutenção e portabilidade.

Este serviço compõe uma parte essencial do ecossistema do projeto ```FIAP X - Sistema de Processamento de Vídeos```, garantindo o recebimento seguro dos arquivos de vídeo que serão posteriormente processados por outros microsserviços do sistema.

Caso deseje contribuir ou integrar este serviço com outros sistemas, fique à vontade para abrir issues, pull requests ou propor melhorias.
