# Script PowerShell para manejo simple de Docker
# Uso: .\docker-vet.ps1 [comando]
# Comandos: build, up, down, logs, clean

param(
    [Parameter(Mandatory=$false)]
    [string]$Command = "help"
)

function Show-Help {
    Write-Host "=== Script Docker Veterinaria ===" -ForegroundColor Green
    Write-Host "Comandos disponibles:" -ForegroundColor Yellow
    Write-Host "  build  - Compilar la aplicación"
    Write-Host "  up     - Iniciar los contenedores"
    Write-Host "  down   - Detener los contenedores"
    Write-Host "  logs   - Ver logs del backend"
    Write-Host "  clean  - Limpiar todo (contenedores, volúmenes, imágenes)"
    Write-Host "  help   - Mostrar esta ayuda"
    Write-Host ""
    Write-Host "Ejemplo: .\docker-vet.ps1 up" -ForegroundColor Cyan
}

function Build-App {
    Write-Host "Compilando aplicación..." -ForegroundColor Yellow
    .\mvnw.cmd clean package -DskipTests
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Compilación exitosa" -ForegroundColor Green
    } else {
        Write-Host "✗ Error en la compilación" -ForegroundColor Red
        exit 1
    }
}

function Start-Services {
    Write-Host "Iniciando servicios con Docker Compose..." -ForegroundColor Yellow
    docker-compose up --build -d
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Servicios iniciados" -ForegroundColor Green
        Write-Host "Backend disponible en: http://localhost:8080" -ForegroundColor Cyan
        Write-Host "Para ver logs: .\docker-vet.ps1 logs" -ForegroundColor Cyan
    }
}

function Stop-Services {
    Write-Host "Deteniendo servicios..." -ForegroundColor Yellow
    docker-compose down
    Write-Host "✓ Servicios detenidos" -ForegroundColor Green
}

function Show-Logs {
    Write-Host "Mostrando logs del backend..." -ForegroundColor Yellow
    docker-compose logs -f backend
}

function Clean-All {
    Write-Host "Limpiando todo..." -ForegroundColor Yellow
    docker-compose down -v
    docker system prune -f
    Write-Host "✓ Limpieza completada" -ForegroundColor Green
}

switch ($Command.ToLower()) {
    "build" { Build-App }
    "up" { 
        Build-App
        Start-Services 
    }
    "down" { Stop-Services }
    "logs" { Show-Logs }
    "clean" { Clean-All }
    "help" { Show-Help }
    default { 
        Write-Host "Comando desconocido: $Command" -ForegroundColor Red
        Show-Help 
    }
}
