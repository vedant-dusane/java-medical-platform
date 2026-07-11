# Section 1

The Medical Management Application is designed to help doctors, hospital staff, and patients manage medical appointments efficiently. The system allows users to schedule and manage appointments, maintain doctor availability, store patient treatment records, and organize healthcare information in one centralized platform.

# Architecture Layers

The application follows a three-layer architecture:

i) Presentation Layer:
The user interacts with the application through a Thymeleaf-based web interface. REST API endpoints receive user requests and pass them to the backend.

ii) Application Layer:
Built using the Spring Framework, this layer contains the business logic, processes user requests, and manages communication between the presentation and data layers.

iii) Data Layer:
This layer uses MySQL and MongoDB to store and manage application data. MySQL is used for structured relational data, while MongoDB stores flexible or document-based data.

# Section 2 – Application Workflow

Step 1:The user interacts with the application through the web interface.

Step 2: The user enters or updates information using the Thymeleaf forms.

Step 3:The submitted request is sent to the appropriate REST API endpoint.

Step 4: The REST API forwards the request to the Spring application layer.

Step 5: The Spring backend processes the request by applying the required business logic.

Step 6: The processed data is validated and prepared for storage or retrieval.

Step 7: The data is stored in or retrieved from MySQL and MongoDB, and the response is returned to the user through the application.
