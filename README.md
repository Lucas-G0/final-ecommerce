# 🚀 Guia Rápido para Rodar o Projeto

## ✅ Pré-requisitos

As seguintes ferramentas devem estar instaladas:

- Java (JDK 17, por exemplo)
- Maven

---

## ☕ 1. Compilar e Executar o Projeto Java com Maven

### Navegue até a raiz do projeto (onde está o `pom.xml`) utilizando um terminal com `Prompt de Comando` ou `Windows Terminal`:

```bash
cd caminho/do/projeto
```

### Compilar o projeto:

```bash
mvn clean install
```

### Executar a aplicação:

```bash
mvn exec:java
```

> Caso o `pom.xml` não tenha o plugin `exec`, você pode executar manualmente com:

```bash
java -cp target/seu-jar-gerado.jar caminho.da.sua.Main
```

---
# final-ecommerce
