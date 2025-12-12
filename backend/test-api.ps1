# Script de test MovieBox API
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "MOVIEBOX API - TESTS" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api"

# Test 1 : Verifier que le serveur repond
Write-Host "`n[1] Test connexion serveur..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/users" -Method Get
    Write-Host "    OK - Serveur accessible" -ForegroundColor Green
} catch {
    Write-Host "    ERREUR - Serveur inaccessible" -ForegroundColor Red
    Write-Host "    Demarre l'application avec: ./mvnw spring-boot:run" -ForegroundColor Yellow
    exit
}

# Test 2 : Ajouter Fight Club
Write-Host "`n[2] Ajout de Fight Club a la watchlist..." -ForegroundColor Yellow
try {
    $movie1 = Invoke-RestMethod -Uri "$baseUrl/user-movies/watchlist/550" -Method Post
    Write-Host "    OK - Ajoute: $($movie1.movie.title)" -ForegroundColor Green
} catch {
    Write-Host "    ERREUR: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 3 : Ajouter Inception
Write-Host "`n[3] Ajout d'Inception a la watchlist..." -ForegroundColor Yellow
try {
    $movie2 = Invoke-RestMethod -Uri "$baseUrl/user-movies/watchlist/27205" -Method Post
    Write-Host "    OK - Ajoute: $($movie2.movie.title)" -ForegroundColor Green
} catch {
    Write-Host "    ERREUR: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 4 : Afficher la watchlist
Write-Host "`n[4] Watchlist actuelle:" -ForegroundColor Yellow
try {
    $watchlist = Invoke-RestMethod -Uri "$baseUrl/user-movies/watchlist" -Method Get
    if ($watchlist.Count -eq 0) {
        Write-Host "    (Aucun film)" -ForegroundColor Gray
    } else {
        foreach ($item in $watchlist) {
            Write-Host "    - $($item.movie.title) (Status: $($item.status))" -ForegroundColor Cyan
        }
    }
} catch {
    Write-Host "    ERREUR: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5 : Noter Fight Club
Write-Host "`n[5] Notation de Fight Club (9/10)..." -ForegroundColor Yellow
try {
    $body = @{ rating = 9.0 } | ConvertTo-Json
    $rated = Invoke-RestMethod -Uri "$baseUrl/user-movies/rate/550" -Method Post -Body $body -ContentType "application/json"
    Write-Host "    OK - Note: $($rated.rating)/10" -ForegroundColor Green
} catch {
    Write-Host "    ERREUR: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 6 : Afficher les films notes
Write-Host "`n[6] Films notes (tries par note):" -ForegroundColor Yellow
try {
    $rated = Invoke-RestMethod -Uri "$baseUrl/user-movies/rated" -Method Get
    if ($rated.Count -eq 0) {
        Write-Host "    (Aucun film note)" -ForegroundColor Gray
    } else {
        foreach ($item in $rated) {
            Write-Host "    - $($item.movie.title): $($item.rating)/10" -ForegroundColor Cyan
        }
    }
} catch {
    Write-Host "    ERREUR: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 7 : Supprimer Inception
Write-Host "`n[7] Suppression d'Inception..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/user-movies/27205" -Method Delete
    Write-Host "    OK - Supprime" -ForegroundColor Green
} catch {
    Write-Host "    ERREUR: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 8 : Verifier la watchlist apres suppression
Write-Host "`n[8] Watchlist apres suppression:" -ForegroundColor Yellow
try {
    $watchlist = Invoke-RestMethod -Uri "$baseUrl/user-movies/watchlist" -Method Get
    if ($watchlist.Count -eq 0) {
        Write-Host "    (Aucun film)" -ForegroundColor Gray
    } else {
        foreach ($item in $watchlist) {
            Write-Host "    - $($item.movie.title)" -ForegroundColor Cyan
        }
    }
} catch {
    Write-Host "    ERREUR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n============================================" -ForegroundColor Cyan
Write-Host "TESTS TERMINES" -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Cyan
