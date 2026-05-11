const API_BASE = "https://gamebar-backend.onrender.com"

const token = localStorage.getItem("userToken");
const topChartsContainer = document.getElementById("topChartsContainer");
const logoutBtn = document.getElementById("logoutBtn");

// Protect page
if (!token) {
    alert("Please login first");
    window.location.href = "login.html";
}

// Load top charts
window.addEventListener("load", function () {
    loadTopCharts();
});

// Logout
if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
        localStorage.removeItem("userToken");
        localStorage.removeItem("userEmail");
        window.location.href = "login.html";
    });
}

// Load games for top charts
function loadTopCharts() {
    topChartsContainer.innerHTML = "<p class='loading-text'>Loading top charts...</p>";

    fetch(`${API_BASE}/games`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to load top charts");
            }
            return response.json();
        })
        .then(games => {
            // V1 logic: sort by price high to low as temporary top chart ranking
            games.sort((a, b) => b.price - a.price);

            renderTopCharts(games);
        })
        .catch(error => {
            console.error("Top charts error:", error);
            topChartsContainer.innerHTML = "<p class='error-text'>Failed to load top charts</p>";
        });
}

// Render cards
function renderTopCharts(games) {
    topChartsContainer.innerHTML = "";

    if (!games || games.length === 0) {
        topChartsContainer.innerHTML = "<p class='empty-text'>No games available in top charts</p>";
        return;
    }

    games.forEach((game, index) => {
        const imagePath = game.imageUrl
            ? `${API_BASE}/images/${game.imageUrl}`
            : "https://placehold.co/400x250?text=No+Image";

        const card = document.createElement("div");
        card.className = "topchart-card";

        card.innerHTML = `
            <div class="rank-badge">#${index + 1}</div>

            <div class="topchart-image-box">
                <img src="${imagePath}" alt="${game.title}" class="topchart-image">
            </div>

            <div class="topchart-info">
                <h3>${game.title}</h3>
                <p>${game.description}</p>
                <h4>₹${game.price}</h4>

                <div class="topchart-actions">
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

        topChartsContainer.appendChild(card);
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