const API_BASE = "https://gamebar-backend.onrender.com"

const token = localStorage.getItem("userToken");
const wishlistContainer = document.getElementById("wishlistContainer");
const logoutBtn = document.getElementById("logoutBtn");

// Protect page
if (!token) {
    alert("Please login first");
    window.location.href = "login.html";
}

// Load wishlist on page load
window.addEventListener("load", function () {
    loadWishlist();
});

// Logout
if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
        localStorage.removeItem("userToken");
        localStorage.removeItem("userEmail");
        window.location.href = "login.html";
    });
}

// Load wishlist games
function loadWishlist() {
    wishlistContainer.innerHTML = "<p class='loading-text'>Loading wishlist...</p>";

    fetch(`${API_BASE}/wishlist/my`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to load wishlist");
        }
        return response.json();
    })
    .then(games => {
        renderWishlist(games);
    })
    .catch(error => {
        console.error("Wishlist load error:", error);
        wishlistContainer.innerHTML = "<p class='error-text'>Failed to load wishlist</p>";
    });
}

// Render wishlist cards
function renderWishlist(games) {
    wishlistContainer.innerHTML = "";

    if (!games || games.length === 0) {
        wishlistContainer.innerHTML = "<p class='empty-text'>Your wishlist is empty</p>";
        return;
    }

    games.forEach(game => {
        const imagePath = game.imageUrl
            ? `${API_BASE}/images/${game.imageUrl}`
            : "https://placehold.co/400x250?text=No+Image";

        const card = document.createElement("div");
        card.className = "wishlist-card";

        card.innerHTML = `
            <div class="wishlist-image-box">
                <img src="${imagePath}" alt="${game.title}" class="wishlist-image">
            </div>

            <div class="wishlist-info">
                <h3>${game.title}</h3>
                <p>${game.description}</p>
                <h4>₹${game.price}</h4>

                <div class="wishlist-actions">
                    <button class="contact-btn" onclick="contactSeller('${game.sellerWhatsapp}', '${game.title}')">Contact Seller</button>
                    <button class="remove-btn" onclick="removeFromWishlist(${game.gameId})">Remove</button>
                </div>
            </div>
        `;

        wishlistContainer.appendChild(card);
    });
}

// Buy game from wishlist
function contactSeller(number, title) {

    if (!number) {
        alert("Seller contact not available");
        return;
    }

    const message = `Hi, I am interested in your game: ${title}`;

    const url = `https://wa.me/${number}?text=${encodeURIComponent(message)}`;

    window.open(url, "_blank");
}

// Remove game from wishlist
function removeFromWishlist(gameId) {
    fetch(`${API_BASE}/wishlist/remove/${gameId}`, {
        method: "DELETE",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(response => response.text())
    .then(message => {
        alert(message);
        loadWishlist();
    })
    .catch(error => {
        console.error("Remove wishlist error:", error);
        alert("Failed to remove game from wishlist");
    });
}