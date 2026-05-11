const API_BASE = "https://gamebar-backend.onrender.com";

const token = localStorage.getItem("userToken");
const userEmail = localStorage.getItem("userEmail");

const titleInput = document.getElementById("title");
const descriptionInput = document.getElementById("description");
const priceInput = document.getElementById("price");
const imageInput = document.getElementById("image");
const editGameIdInput = document.getElementById("editGameId");

const addGameBtn = document.getElementById("addGameBtn");
const cancelEditBtn = document.getElementById("cancelEditBtn");
const logoutBtn = document.getElementById("logoutBtn");

const myUploadedGamesContainer = document.getElementById("myUploadedGamesContainer");

if (!token) {
    alert("Please login first");
    window.location.href = "login.html";
}

window.addEventListener("load", function () {
    loadMyUploadedGames();
});

if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
        localStorage.removeItem("userToken");
        localStorage.removeItem("userEmail");
        window.location.href = "login.html";
    });
}

if (addGameBtn) {
    addGameBtn.addEventListener("click", function () {
        const editGameId = editGameIdInput.value;

        if (editGameId) {
            updateGame(editGameId);
        } else {
            addGame();
        }
    });
}

if (cancelEditBtn) {
    cancelEditBtn.addEventListener("click", function () {
        resetForm();
    });
}

// ================= ADD GAME =================

function addGame() {
    const title = titleInput.value.trim();
    const description = descriptionInput.value.trim();
    const price = priceInput.value.trim();
    const imageFile = imageInput.files[0];

    if (title === "" || description === "" || price === "") {
        alert("Please fill title, description, and price");
        return;
    }

    if (Number(price) <= 0) {
        alert("Price must be greater than 0");
        return;
    }

    addGameBtn.innerText = "Adding...";
    addGameBtn.disabled = true;

    fetch(`${API_BASE}/games/add`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
            title: title,
            description: description,
            price: Number(price)
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to add game");
        }

        return response.json();
    })
    .then(savedGame => {
        if (imageFile) {
            uploadGameImage(savedGame.id, imageFile, "Game added successfully");
        } else {
            alert("Game added successfully");
            resetForm();
            loadMyUploadedGames();
        }
    })
    .catch(error => {
        console.error("Add game error:", error);
        alert("Failed to add game");
    })
    .finally(() => {
        addGameBtn.innerText = "Add Game";
        addGameBtn.disabled = false;
    });
}

// ================= UPLOAD IMAGE =================

function uploadGameImage(gameId, imageFile, successMessage) {
    const formData = new FormData();
    formData.append("file", imageFile);

    fetch(`${API_BASE}/games/upload/${gameId}`, {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token
        },
        body: formData
    })
    .then(response => response.text())
    .then(message => {
        alert(successMessage || message || "Image uploaded successfully");
        resetForm();
        loadMyUploadedGames();
    })
    .catch(error => {
        console.error("Image upload error:", error);
        alert("Game saved, but image upload failed");
        resetForm();
        loadMyUploadedGames();
    });
}

// ================= LOAD MY UPLOADED GAMES =================

function loadMyUploadedGames() {
    myUploadedGamesContainer.innerHTML = "<p class='loading-text'>Loading your uploaded games...</p>";

    fetch(`${API_BASE}/games`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to load games");
        }

        return response.json();
    })
    .then(games => {
        const myGames = games.filter(game => game.sellerEmail === userEmail);
        renderMyUploadedGames(myGames);
    })
    .catch(error => {
        console.error("Load uploaded games error:", error);
        myUploadedGamesContainer.innerHTML = "<p class='error-text'>Failed to load uploaded games</p>";
    });
}

// ================= RENDER MY UPLOADED GAMES =================

function renderMyUploadedGames(games) {
    myUploadedGamesContainer.innerHTML = "";

    if (!games || games.length === 0) {
        myUploadedGamesContainer.innerHTML = "<p class='empty-text'>You have not uploaded any games yet.</p>";
        return;
    }

    games.forEach(game => {
        const imagePath = game.imageUrl
            ? `${API_BASE}/images/${game.imageUrl}`
            : "https://placehold.co/400x250?text=No+Image";

        const card = document.createElement("div");
        card.className = "uploaded-game-card";

        card.innerHTML = `
            <div class="uploaded-game-image-box">
                <img src="${imagePath}" alt="${game.title}" class="uploaded-game-image">
            </div>

            <div class="uploaded-game-info">
                <h3>${game.title}</h3>
                <p>${game.description}</p>
                <h4>₹${game.price}</h4>

                <div class="uploaded-game-actions">
                    <button class="edit-game-btn" onclick="startEditGame(${game.id}, '${escapeText(game.title)}', '${escapeText(game.description)}', ${game.price})">
                        Edit
                    </button>

                    <button class="delete-game-btn" onclick="deleteMyGame(${game.id})">
                        Delete
                    </button>
                </div>
            </div>
        `;

        myUploadedGamesContainer.appendChild(card);
    });
}

// ================= START EDIT =================

function startEditGame(gameId, title, description, price) {
    editGameIdInput.value = gameId;
    titleInput.value = title;
    descriptionInput.value = description;
    priceInput.value = price;

    addGameBtn.innerText = "Update Game";
    cancelEditBtn.style.display = "inline-block";

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}


// ================= UPDATE GAME =================

function updateGame(gameId) {
    const title = titleInput.value.trim();
    const description = descriptionInput.value.trim();
    const price = priceInput.value.trim();
    const imageFile = imageInput.files[0];

    if (title === "" || description === "" || price === "") {
        alert("Please fill title, description, and price");
        return;
    }

    if (Number(price) <= 0) {
        alert("Price must be greater than 0");
        return;
    }

    addGameBtn.innerText = "Updating...";
    addGameBtn.disabled = true;

    fetch(`${API_BASE}/games/update/${gameId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
            title: title,
            description: description,
            price: Number(price)
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to update game");
        }

        return response.text();
    })
    .then(message => {
        if (imageFile) {
            uploadGameImage(gameId, imageFile, "Game updated successfully");
        } else {
            alert(message || "Game updated successfully");
            resetForm();
            loadMyUploadedGames();
        }
    })
    .catch(error => {
        console.error("Update game error:", error);
        alert("Failed to update game");
    })
    .finally(() => {
        addGameBtn.innerText = editGameIdInput.value ? "Update Game" : "Add Game";
        addGameBtn.disabled = false;
    });
}

// ================= DELETE MY GAME =================

function deleteMyGame(gameId) {
    const confirmDelete = confirm("Delete this uploaded game?");

    if (!confirmDelete) {
        return;
    }

    fetch(`${API_BASE}/games/delete/${gameId}`, {
        method: "DELETE",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(response => response.text())
    .then(message => {
        alert(message || "Game deleted successfully");
        loadMyUploadedGames();
    })
    .catch(error => {
        console.error("Delete game error:", error);
        alert("Failed to delete game");
    });
}

// ================= RESET FORM =================

function resetForm() {
    editGameIdInput.value = "";
    titleInput.value = "";
    descriptionInput.value = "";
    priceInput.value = "";
    imageInput.value = "";

    addGameBtn.innerText = "Add Game";
    addGameBtn.disabled = false;

    cancelEditBtn.style.display = "none";
}

// ================= ESCAPE TEXT =================

function escapeText(text) {
    if (!text) {
        return "";
    }

    return String(text)
        .replace(/\\/g, "\\\\")
        .replace(/'/g, "\\'")
        .replace(/"/g, "&quot;")
        .replace(/\n/g, " ");
}