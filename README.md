📘 DSA Training System Demo
📌 Project Overview

DSA Training System Demo is a Java-based backend application designed to support learning and practicing Data Structures & Algorithms (DSA) through an organized training system. This project provides a foundation for building, testing, and tracking training modules and problem-solving exercises in DSA.

🧠 Key Features

🗂 Core backend logic implemented in Java

📌 Supports modular training and DSA problem organization

⚙️ Clean and extensible architecture suitable for learning and future expansion

📦 Uses Maven for dependency and build management

🔐 Contains example configuration for environment variables and keys

📁 Prepared folder structure for data, infrastructure, and application code

🛠️ Tech Stack

Language: Java

Build Tool: Maven

Architecture: Layered backend (example structure – modeling controllers, services, repositories optionally)

Environment: Local execution with environment variables

Version Control: Git & GitHub

🏗️ Project Structure

Typical folders you will see in this repository:

├── data/                # Example dataset or resource files
├── infra/               # Infrastructure related configs or tools
├── piston/              # Business domain or engine modules
├── src/                 # Main application source code (controllers, services, entities)
├── .mvn/                # Maven wrapper
├── pom.xml              # Project dependency & build config
├── local.env            # Environment config (private, not tracked)
├── private-key.pem      # Example secrets
├── public-key.pem       # Public certificate/keys


This layout makes it easier to scale the project into a full training system including APIs, database, and frontend in the future.

🚀 Installation & Setup

To run the project locally:

Clone the repository

git clone https://github.com/ngleanhvu/dsa_training_system_demo.git


Navigate to project

cd dsa_training_system_demo


Configure environment

Create .env or local.env with required variables

Set any required keys or credentials (e.g., for authentication)

Build the project

mvn clean install


Run the application

mvn spring-boot:run

🧪 How to Use

This project currently serves as a backend skeleton:

You can extend it with REST endpoints for managing:

DSA topics and lessons

Training records

User submissions and progress

Connect to a database like MySQL/PostgreSQL

Add frontend UI (React/Angular/Vue) or mobile app later

👤 Author

Nguyễn Lê Anh Vũ – Backend Developer & DSA training system architect

📄 License

This repository currently does not include a LICENSE file — consider adding one if you want to open-source it publicly.

🤝 Contribution

If you’d like to contribute enhancements, bug fixes or improvements:

Fork the project

Create a feature branch

Submit a pull request

📝 Short Summary (for GitHub repo description)

Backend demo project for a Data Structures & Algorithms training system built with Java and Maven — foundation for building modular training APIs and problem tracking.
