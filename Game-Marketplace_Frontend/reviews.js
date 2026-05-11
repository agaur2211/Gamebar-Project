const API_BASE = "https://gamebar-backend.onrender.com"
const token = localStorage.getItem("userToken");
const selectedGameId = localStorage.getItem("selectedGameId");

const gameTitle = document.getElementById("gameTitle");
const averageRating = document.getElementById("averageRating");
const reviewsContainer = document.getElementById("reviewsContainer");
const reviewForm = document.getElementById("reviewForm");
const ratingInput = document.getElementById("rating");
const commentInput = document.getElementById("comment");
const logoutBtn = document.getElementById("logoutBtn");

// Page protection
if (!token) {
    alert("Please login first");
    window.location.href = "login.html";
}

// If no game selected
if (!selectedGameId) {
    alert("No game selected");
    window.location.href = "index.html";
}

// Load data when page opens
window.addEventListener("load", function () {
    loadGameDetails();
    loadAverageRating();
    loadReviews();
});

// Logout
if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
        localStorage.removeItem("userToken");
        localStorage.removeItem("userEmail");
        localStorage.removeItem("selectedGameId");
        window.location.href = "login.html";
    });
}

// Submit review
if (reviewForm) {
    reviewForm.addEventListener("submit", function (event) {
        event.preventDefault();
        submitReview();
    });
}

// Load game title from backend
function loadGameDetails() {
    fetch(`${API_BASE}/games/${selectedGameId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to load game details");
            }
            return response.json();
        })
        .then(game => {
            gameTitle.innerText = `${game.title} Reviews`;
        })
        .catch(error => {
            console.error("Game details error:", error);
            gameTitle.innerText = "Game Reviews";
        });
}

// Load average rating
function loadAverageRating() {
    fetch(`${API_BASE}/reviews/average/${selectedGameId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to load average rating");
            }
            return response.text();
        })
        .then(avg => {
            const rating = Number(avg);

            if (!rating || rating === 0) {
                averageRating.innerText = "Average Rating: Not rated yet";
            } else {
                averageRating.innerText = `Average Rating: ${rating.toFixed(1)} / 5 ⭐`;
            }
        })
        .catch(error => {
            console.error("Average rating error:", error);
            averageRating.innerText = "Average Rating: Not available";
        });
}

// Load all reviews for selected game
function loadReviews() {
    reviewsContainer.innerHTML = "<p class='loading-text'>Loading reviews...</p>";

    fetch(`${API_BASE}/reviews/${selectedGameId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to load reviews");
            }
            return response.json();
        })
        .then(reviews => {
            renderReviews(reviews);
        })
        .catch(error => {
            console.error("Reviews error:", error);
            reviewsContainer.innerHTML = "<p class='error-text'>Failed to load reviews</p>";
        });
}

// Render reviews
function renderReviews(reviews) {
    reviewsContainer.innerHTML = "";

    if (!reviews || reviews.length === 0) {
        reviewsContainer.innerHTML = "<p class='empty-text'>No reviews yet. Be the first to review this game.</p>";
        return;
    }

    reviews.forEach(review => {
        const reviewCard = document.createElement("div");
        reviewCard.className = "review-card";

        const stars = "⭐".repeat(review.rating);

        reviewCard.innerHTML = `
            <div class="review-header">
                <h3>${review.userEmail}</h3>
                <span>${stars}</span>
            </div>

            <p class="review-comment">${review.comment}</p>

            <small class="review-date">
                ${formatDate(review.createdAt)}
            </small>
        `;

        reviewsContainer.appendChild(reviewCard);
    });
}

// Submit new review
function submitReview() {
    const rating = Number(ratingInput.value);
    const comment = commentInput.value.trim();

    if (!rating || rating < 1 || rating > 5) {
        alert("Please select a valid rating");
        return;
    }

    if (comment === "") {
        alert("Please write a comment");
        return;
    }

    fetch(`${API_BASE}/reviews/add/${selectedGameId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
            rating: rating,
            comment: comment
        })
    })
    .then(response => response.text())
    .then(message => {
        alert(message);

        commentInput.value = "";
        ratingInput.value = "5";

        loadAverageRating();
        loadReviews();
    })
    .catch(error => {
        console.error("Submit review error:", error);
        alert("Failed to submit review");
    });
}

// Format date nicely
function formatDate(dateValue) {
    if (!dateValue) {
        return "";
    }

    const date = new Date(dateValue);

    return date.toLocaleString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });
}