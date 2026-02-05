# LorisGate

**LorisGate** is a lightweight, high-performance OpenID Connect (OIDC) and Identity Management (IDM) server.

## Features

- OpenID Connect (OIDC) compliant authentication
- High-performance, scalable architecture
- Extensible identity and user management
- Java-based project built with Maven
- Apache 2.0 licensed

## Getting Started

### Requirements

- Java 25 or newer
- Maven 3.9+

### Build from Source

```bash
git clone https://github.com/lorislab/lorisgate.git
cd lorisgate
mvn clean install
```

### Run

```bash
java -jar target/lorisgate-*.jar
```

## Configuration

LorisGate can be configured using environment variables or application configuration files depending on your deployment setup.

Common configuration areas include:

- OIDC issuer settings
- Client registrations
- Database or user store connection
- Security keys and certificates

## Development

Run tests:

```bash
mvn test
```

You can import the project into IntelliJ IDEA, Eclipse, or VS Code as a Maven project.

## Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes
4. Push to your fork
5. Open a Pull Request

## License

Licensed under the Apache License, Version 2.0.
