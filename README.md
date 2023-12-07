Captcha-like Windows Service
Overview
This project is a combination of a Spring Boot backend and a JavaFX , designed to create a captcha-like Windows service. The service prompts the user with a captcha-like window at random intervals, verifying the user's presence in front of the computer.

Features
Random Popup: The service displays a captcha-like window at a random location on the screen every 30 minutes.
User Verification: The user is required to respond within 30 seconds, confirming their presence.
MAC Address Tracking: The service sends a request to the specified port along with the user's MAC address upon user response.
Timeout Handling: If the user does not respond within the given time frame, the service assumes the user is not present and sends notification requests.

Prerequisites
Java Development Kit (JDK) 8 or higher
Maven
Spring Boot
JavaFX
Setup
Clone the Repository:

bash
Copy code
git clone https://github.com/your-username/your-repository.git
Build the Project:

bash
Copy code
cd your-repository
mvn clean install
Run the Application:

bash
Copy code
java -jar target/your-application.jar
Access the Service:
Open a web browser and navigate to http://localhost:8080 to interact with the service.

Configuration
The port number and other configurations can be modified in the application.properties file.
Usage
Launch the application.
The captcha-like window will appear randomly on the screen every 30 minutes.
Respond within 30 seconds to confirm your presence.
The service will send requests with your MAC address upon successful verification.
If no response is received, notification requests will be sent.
