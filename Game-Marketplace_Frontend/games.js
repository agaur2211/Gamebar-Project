const API_BASE = "https://gamebar-backend.onrender.com"

const token = localStorage.getItem("userToken");

const gamesContainer = document.getElementById("gamesContainer");
const logoutBtn = document.getElementById("logoutBtn");

const searchInput = document.getElementById("searchInput");
const searchBtn = document.getElementById("searchBtn");

const minPrice = document.getElementById("minPrice");
const maxPrice = document.getElementById("maxPrice");
const filterBtn = document.getElementById("filterBtn");

const sortSelect = document.getElementById("sortSelect");

// Protect page
if (!token) {
    alert("Please login first");
    window.location.href = "login.html";
}

// Load games on page open
window.addEventListener("load", function () {
    loadGames();
});

// Logout
if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
        localStorage.removeItem("userToken");
        localStorage.removeItem("userEmail");
        window.location.href = "login.html";
    });
}

// Load all games
function loadGames() {
    gamesContainer.innerHTML = "<p class='loading-text'>Loading games...</p>";

    fetch(`${API_BASE}/games`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to load games");
            }
            return response.json();
        })
        .then(games => {
            renderGames(games);
        })
        .catch(error => {
            console.error("Load games error:", error);
            gamesContainer.innerHTML = "<p class='error-text'>Failed to load games</p>";
        });
}

// Render cards
function renderGames(games) {
    gamesContainer.innerHTML = "";

    if (!games || games.length === 0) {
        gamesContainer.innerHTML = "<p class='empty-text'>No games found</p>";
        return;
    }

    games.forEach(game => {
        const imagePath = game.imageUrl
            ? `${API_BASE}/images/${game.imageUrl}`
            : "https://placehold.co/400x250?text=No+Image";

        const card = document.createElement("div");
        card.className = "game-card";

        card.innerHTML = `
            <div class="game-image-box">
                <img src="${imagePath}" alt="${game.title}" class="game-image">
            </div>

            <div class="game-info">
                <h3>${game.title}</h3>
                <p>${game.description}</p>
                <h4>₹${game.price}</h4>

                <div class="game-actions">
                    <button class="contact-btn" onclick="contactSeller('${game.sellerWhatsapp}', '${game.title}')">
                        Contact Seller
                    </button>

                    <button class="wishlist-btn" onclick="addToWishlist(${game.id})">
                        Wishlist
                    </button>

                    <button class="review-btn" onclick="viewReviews(${game.id})">
                        Reviews
                    </button>
                </div>
            </div>
        `;

        gamesContainer.appendChild(card);
    });
}

// Contact seller through WhatsApp
function contactSeller(number, title) {
    if (!number) {
        alert("Seller contact not available");
        return;
    }

    const message = `Hi, I am interested in your game: ${title}`;
    const url = `https://wa.me/${number}?text=${encodeURIComponent(message)}`;

    window.open(url, "_blank");
}

// Add to wishlist
function addToWishlist(gameId) {
    fetch(`${API_BASE}/wishlist/add/${gameId}`, {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(response => response.text())
    .then(message => {
        alert(message);
    })
    .catch(error => {
        console.error("Wishlist error:", error);
        alert("Failed to add to wishlist");
    });
}

// Open reviews page
function viewReviews(gameId) {
    localStorage.setItem("selectedGameId", gameId);
    window.location.href = "reviews.html";
}

// Search
if (searchBtn) {
    searchBtn.addEventListener("click", function () {
        const title = searchInput.value.trim();

        if (title === "") {
            loadGames();
            return;
        }

        fetch(`${API_BASE}/games/search?title=${encodeURIComponent(title)}`)
            .then(response => response.json())
            .then(games => renderGames(games))
            .catch(error => {
                console.error("Search error:", error);
                alert("Search failed");
            });
    });
}

// Filter
if (filterBtn) {
    filterBtn.addEventListener("click", function () {
        const min = minPrice.value;
        const max = maxPrice.value;

        if (min === "" || max === "") {
            alert("Please enter both min and max price");
            return;
        }

        fetch(`${API_BASE}/games/filter?minPrice=${min}&maxPrice=${max}`)
            .then(response => response.json())
            .then(games => renderGames(games))
            .catch(error => {
                console.error("Filter error:", error);
                alert("Filter failed");
            });
    });
}

// Sort
if (sortSelect) {
    sortSelect.addEventListener("change", function () {
        const value = sortSelect.value;

        if (value === "") {
            loadGames();
            return;
        }

        let sort = "price";
        let direction = "asc";

        if (value === "price-asc") {
            sort = "price";
            direction = "asc";
        } else if (value === "price-desc") {
            sort = "price";
            direction = "desc";
        } else if (value === "title-asc") {
            sort = "title";
            direction = "asc";
        } else if (value === "title-desc") {
            sort = "title";
            direction = "desc";
        }

        fetch(`${API_BASE}/games?sort=${sort}&direction=${direction}`)
            .then(response => response.json())
            .then(games => renderGames(games))
            .catch(error => {
                console.error("Sort error:", error);
                alert("Sort failed");
            });
    });
}