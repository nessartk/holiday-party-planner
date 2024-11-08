# Plataforma de Organização de Festas de Fim de Ano

## Descrição

"Plataforma de Organização de Festas de Fim de Ano" é uma aplicação que oferece uma interface web (SPRING) responsável pelo planejamento e organização de festas de fim de ano. Projeto desenvolvido em JAVA e SPRING, utilizando bibliotecas externas para a administração de bancos de dados.

## A Aplicação

### Identificação dos Atores
- Organizador do Evento: Cria e gerencia o evento.( criar, editar e deletar o evento)
- Convidado: Confirma a presença e escolhe os itens que levará para o evento.

### Definição do Caso de Uso
   Nome: Criar Evento e Selecionar Itens

### Atores do Caso de Uso
- Organizador do Evento: pode "Criar Evento", "Editar Evento" e "Deletar Evento". 
- Convidado: pode "Visualizar Evento", "Confirmar Presença" e "Selecionar Item da Lista".

### Descrição do Caso de Uso

   Resumo: O organizador cria um evento e define uma lista de itens. Os convidados podem acessar essa lista e escolher os itens que levarão.

### Fluxo Principal - Requisitos Funcionais
   
1. O organizador acessa o sistema.
2. O organizador cria um novo evento, inserindo informações como nome, data, local e descrição.
3. O organizador adiciona uma lista de itens disponíveis para o evento.
4. Os convidados são notificados sobre o evento.
5. O convidado acessa o evento.
6. O convidado visualiza a lista de itens.
7. O convidado seleciona os itens que deseja levar.
8. O sistema confirma a seleção e atualiza a lista de itens disponíveis.

## Tecnologias Utilizadas

- **Java**: Linguagem de programação principal utilizada para desenvolver a lógica do sistema.

- **Spring Framework**: Ecossistema de desenvolvimento para facilitar a criação de aplicações Java utilizando diversos módulos independentes.

- **PostgreSQL**: Banco de dados robusto para melhor gerenciamento de grandes quantidades de dados.

## Regras de Negócio

- **RN1**: Persistência em banco de dados (H2 ou Postgres);
- **RN2**: Configuração de Segurança: Controle de rota e login (jwt opcional);
- **RN3**: Consumo de uma API externa pública;
- **RN4**: Swagger opcional;
- **RN5**: Frontend opcional.
- **Objetivo**:
    - Criar uma API REST contendo os itens acima citados;
    - O organizador acessa o sistema;
    - O organizador cria um novo evento, inserindo informações como nome, data, local e descrição;
    - O organizador adiciona uma lista de itens disponíveis para o evento;
    - Os convidados são notificados sobre o evento;
    - O convidado acessa o evento;
    - O convidado visualiza a lista de itens;
    - O convidado seleciona os itens que deseja levar;
    - O sistema confirma a seleção e atualiza a lista de itens disponíveis;
    - Se o convidado tentar selecionar mais itens do que o permitido: O sistema exibe uma mensagem de erro e solicita que o convidado reduza a seleção;
    - Se o organizador quiser editar o evento: O organizador pode modificar detalhes do evento ou a lista de itens.

## Instalação
### Pré-Requisitos
- Java Development Kit (JDK) instalado.
- IDE (como IntelliJ IDEA) configurada para desenvolvimento em Java.
- PostgresSQL instalado e configurado.

## Projeto desenvolvido por:

[<img alt="Alan Filho" height="75px" src="https://avatars.githubusercontent.com/u/125782386?v=4" width="75px"/>](https://github.com/oalleeN)
[<img alt="Christina Carvalho" height="75px" src="https://avatars.githubusercontent.com/u/175761726?v=4" width="75px"/>](https://github.com/ChristinaC-dev)
[<img alt="Karine Amorim" height="75px" src="https://avatars.githubusercontent.com/u/138794780?v=4" width="75px"/>](https://github.com/Kahmori)
[<img alt="Lucas Campos" height="75px" src="https://avatars.githubusercontent.com/u/161725621?v=4" width="75px"/>](https://github.com/lucascodebr20)
[<img alt="Maria Eduarda" height="75px" src="https://avatars.githubusercontent.com/u/134453107?v=4" width="75px"/>](https://github.com/mariaemrqs)
[<img alt="Thais Vieira" height="75px" src="https://avatars.githubusercontent.com/u/104239787?v=4" width="75px"/>](https://github.com/throv)
[<img alt="Vanessa Rutkoski" height="75px" src="https://avatars.githubusercontent.com/u/98660246?v=4" width="75px"/>](https://github.com/nessartk)


## **Licença**

Este projeto é licenciado sob a [MIT License](LICENSE).