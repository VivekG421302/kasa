# Kasa - Telegram-based Backend

Kasa is a Spring Boot backend application designed for internal office use (10-20 users). It uses the Telegram Bot API as a communication and media storage layer, providing a persistent backend for a separate frontend application (e.g., Next.js).

## Core Architecture

The application is a monolith built with Java 17 and Spring Boot. It uses MySQL for metadata persistence and Hibernate/JPA for data access.

The key concept is using Telegram for what it's good at: real-time messaging and cloud file storage. The application itself does not store any binary files (images, documents, etc.). It only stores the metadata, including the `file_id` provided by Telegram.

### How Telegram is Used

1.  **Transport Layer**: All communication between users happens via a single Telegram bot. The application uses webhooks to receive updates from the bot in real-time.
2.  **Media Storage**: When a user sends a file (image, video, document, etc.), the file is stored on Telegram's servers. The application saves the `file_id`, which is a pointer to that file. To send or retrieve that file later, the application simply uses this ID.
3.  **User Interaction**: Users interact with the application by messaging the bot directly. The application automatically registers users on their first interaction.

### Chat Model

The current implementation supports 1-to-1 chats between a regular user and a designated "admin" user.

-   When a user sends a message to the bot for the first time, a new `User` record is created.
-   A `ChatRoom` is established between this user and the admin user (defined by `kasa.bot.admin-telegram-id` in the configuration).
-   All subsequent messages from the user are routed to this chat room.

This model is suitable for scenarios like internal support, HR requests, or any case where a user needs to communicate with a central entity. The admin can use the REST API (via a frontend) to see all chat rooms and messages.

## How to Run Locally

### Prerequisites

-   Java 17
-   Maven
-   MySQL Server
-   A Telegram Bot Token

### 1. Create a Telegram Bot

1.  Talk to the [@BotFather](https://t.me/BotFather) on Telegram.
2.  Create a new bot using the `/newbot` command.
3.  Note down the **token** provided. It will look something like `123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11`.
4.  Disable group privacy for the bot by sending `/setprivacy` to BotFather, selecting your bot, and choosing "Disable". This allows the bot to receive all messages when in a group, although this app is designed for private chats.

### 2. Configure the Application

1.  Open `src/main/resources/application.yml`.
2.  Fill in the required properties:
    -   `telegram.bot.username`: Your bot's username (e.g., `@MyKasaBot`).
    -   `telegram.bot.token`: The token you received from BotFather.
    -   `datasource.username`: Your MySQL username.
    -   `datasource.password`: Your MySQL password.
    -   `kasa.bot.admin-telegram-id`: The Telegram User ID of the account that will act as the "admin" or central point of contact. You can get your ID by messaging `@userinfobot`.

### 3. Build and Run

1.  Open a terminal in the project root directory.
2.  Build the application using Maven:
    ```bash
    ./mvnw clean install
    ```
3.  Run the application:
    ```bash
    java -jar target/kasa-0.0.1-SNAPSHOT.jar
    ```
The application will start on port 8080.

### 4. Set Up the Webhook

For Telegram to send updates to your local machine, you need to expose your local server to the internet. [ngrok](https://ngrok.com/) is a great tool for this.

1.  Install and configure ngrok.
2.  Run it to expose your local port 8080:
    ```bash
    ngrok http 8080
    ```
3.  ngrok will give you a public URL, like `https://abcdef123456.ngrok.io`.
4.  Use this URL to tell Telegram where to send updates. Construct the following URL and open it in your browser (or use `curl`):

    ```
    https://api.telegram.org/bot<YOUR_BOT_TOKEN>/setWebhook?url=<YOUR_NGROK_URL>/telegram-webhook
    ```
    Replace `<YOUR_BOT_TOKEN>` and `<YOUR_NGROK_URL>`. The `/telegram-webhook` path must match the `telegram.bot.webhook-path` in your `application.yml`.

    Example:
    ```
    https://api.telegram.org/bot123456:ABC-DEF/setWebhook?url=https://abcdef123456.ngrok.io/telegram-webhook
    ```

5.  You should see a success message: `{"ok":true,"result":true,"description":"Webhook was set"}`.

Now, any message you send to your bot will be forwarded to your running Spring Boot application.

## Internal REST API

The application exposes a REST API for a frontend to consume.

-   `GET /api/users`: List all registered users.
-   `GET /api/chats`: List all active chat rooms.
-   `GET /api/chats/{chatId}/messages`: Get all messages for a specific chat room.
-   `POST /api/messages`: Send a text message from one user to another (e.g., from the admin to a user).

## Known Limitations

-   **1-to-1 with Admin Only**: The current logic only supports chats between a user and the designated admin. It does not support user-to-user chats initiated from Telegram. This would require a command-based system (e.g., `/chat @username`) which is not yet implemented.
-   **No Group Chats**: The application is not designed to handle group chats.
-   **Admin-Initiated Chat**: The admin cannot currently initiate a conversation from Telegram; they can only reply to incoming messages via the REST API. The `MessageService` ignores messages sent from the admin's account directly to the bot.
-   **Error Handling**: Basic error logging is in place, but there is no sophisticated feedback mechanism to the user via Telegram if something goes wrong.
-   **Security**: The API endpoints are not secured. For production use, Spring Security should be added.
