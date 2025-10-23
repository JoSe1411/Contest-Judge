# ContestJudge System

A full-stack online contest judge platform that allows users to solve programming problems, submit code, and receive execution results through a web interface.

## Project Overview

This system provides a complete contest programming environment with:
- Interactive code editor with syntax highlighting
- Multi-language support (Java, Python, C++)
- Real-time code execution using Docker containers
- AWS S3 integration for file storage
- RESTful API for code submission and result retrieval

## Architecture

### Frontend (React + TypeScript)
- **Framework**: React 19 with TypeScript
- **Build Tool**: Vite 7
- **Styling**: Tailwind CSS
- **Code Editor**: Monaco Editor
- **State Management**: TanStack React Query
- **Routing**: React Router
- **HTTP Client**: Axios
- **Mocking**: MSW (Mock Service Worker)

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.3.0
- **Language**: Java 17
- **Build Tool**: Maven
- **Container Runtime**: Docker Java
- **Cloud Storage**: AWS S3
- **JSON Processing**: Jackson
- **Validation**: Jakarta Bean Validation

## Features

- Interactive problem solving interface
- Multi-language code execution
- Real-time result visualization
- Test case management
- Code submission and judging
- Responsive design with dark theme

## Current Development Status

**Important**: The working implementation with full frontend-backend integration is available in the `frontend-layout-improvements` branch.

### Branch Information
- **main**: Base project structure
- **frontend-layout-improvements**: Working implementation with CORS configuration and API integration

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Docker Desktop
- AWS CLI configured (for S3 functionality)
- Maven 3.6+

### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

Frontend will start on `http://localhost:5173`

## API Endpoints

### Problem Management
- `GET /problems/{id}` - Retrieve problem details
- `GET /` - Health check endpoint

### Code Execution
- `POST /submit` - Submit code for execution
- `POST /run-compare` - Run code with custom input

## Development Workflow

1. **Start Backend**: `mvn spring-boot:run` (port 8080)
2. **Start Frontend**: `npm run dev` (port 5173)
3. **Configure CORS**: Ensure frontend can communicate with backend
4. **Test Integration**: Use the web interface to submit and run code

## Project Structure

```
ContestJudge/
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   └── org/example/
│   │       ├── config/      # Configuration classes
│   │       ├── controller/  # REST controllers
│   │       ├── service/     # Business logic
│   │       ├── dto/         # Data transfer objects
│   │       └── Main.java    # Application entry point
│   ├── pom.xml             # Maven dependencies
│   └── src/main/resources/
├── frontend/               # React application
│   ├── src/
│   │   ├── api/           # API client and endpoints
│   │   ├── components/    # React components
│   │   ├── hooks/         # Custom React hooks
│   │   ├── pages/         # Page components
│   │   ├── types/         # TypeScript type definitions
│   │   └── mocks/         # Mock service worker
│   ├── package.json       # Node.js dependencies
│   └── vite.config.ts     # Vite configuration
├── curl_commands.txt       # API testing examples
└── docker/                # Docker-related files
```

## Key Technologies

- **Spring Boot**: Backend framework with auto-configuration
- **Docker Integration**: Dynamic container creation for code execution
- **Monaco Editor**: Professional code editing experience
- **TanStack React Query**: Server state management
- **AWS S3**: File storage for code submissions
- **Tailwind CSS**: Utility-first styling framework

## Testing Examples

See `curl_commands.txt` for comprehensive API testing examples including:
- Two Sum problem solutions in multiple languages
- Test case formats and expected outputs
- Both `/submit` and `/run-compare` endpoint usage

## Contributing

1. Create a feature branch from `frontend-layout-improvements`
2. Make your changes
3. Test both frontend and backend integration
4. Submit a pull request

## Development Notes

- The system uses Docker containers to isolate code execution
- Frontend includes mock services for development without backend
- CORS configuration allows cross-origin requests between frontend and backend
- Code execution results are processed as JSON responses

## Troubleshooting

- Ensure Docker Desktop is running for code execution
- Check CORS configuration if frontend-backend communication fails
- Verify AWS credentials for S3 functionality
- Check browser console for detailed error messages

## License

This project is for educational purposes and contest programming system development.
