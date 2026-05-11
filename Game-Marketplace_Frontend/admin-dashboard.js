const API_BASE = "https://gamebar-backend.onrender.com"
const adminToken = localStorage.getItem("adminToken");

const totalUsers = document.getElementById("totalUsers");
const totalGames = document.getElementById("totalGames");

const usersContainer = document.getElementById("usersContainer");
const gamesContainer = document.getElementById("gamesContainer");

const logoutBtn = document.getElementById("logoutBtn");

// ====================== PROTECT PAGE ======================

if (!adminToken) {
    alert("Admin login required");
    window.location.href = "admin-login.html";
}

// ====================== PAGE LOAD ======================

window.addEventListener("load", function () {
    loadUsers();
    loadGames();
});

// ====================== LOGOUT ======================

if (logoutBtn) {

    logoutBtn.addEventListener("click", function () {

        localStorage.removeItem("adminToken");

        window.location.href = "admin-login.html";
    });
}

// ====================== LOAD USERS ======================

function loadUsers() {

    usersContainer.innerHTML = "<p>Loading users...</p>";

    fetch(`${API_BASE}/admin/users`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + adminToken
        }
    })
    .then(response => {

        if (!response.ok) {
            throw new Error("Failed to load users");
        }

        return response.json();
    })
    .then(users => {

        totalUsers.innerText = users.length;

        renderUsers(users);
    })
    .catch(error => {

        console.error("Users error:", error);

        usersContainer.innerHTML = "<p>Failed to load users</p>";
    });
}

// ====================== RENDER USERS ======================

function renderUsers(users) {

    usersContainer.innerHTML = "";

    if (!users || users.length === 0) {

        usersContainer.innerHTML = "<p>No users found</p>";

        return;
    }

    users.forEach(user => {

        const card = document.createElement("div");

        card.className = "admin-user-card";

        const statusText = user.banned ? "Banned" : "Active";

        card.innerHTML = `
        
            <div class="admin-user-info">
            
                <h3>${user.name}</h3>

                <p>${user.email}</p>

                <small>
                    ${user.whatsappNumber || "No WhatsApp"}
                </small>

                <br>

                <strong>Status:</strong>
                ${statusText}

            </div>

            <div class="admin-user-actions">

                <button class="make-admin-btn"
                    onclick="makeAdmin(${user.id})">

                    Make Admin

                </button>

                <button class="remove-admin-btn"
                    onclick="removeAdmin(${user.id})">

                    Remove Admin

                </button>

                <button class="ban-btn"
                    onclick="banUser(${user.id})">

                    Ban

                </button>

                <button class="unban-btn"
                    onclick="unbanUser(${user.id})">

                    Unban

                </button>

                <button class="delete-user-btn"
                    onclick="deleteUser(${user.id})">

                    Remove User

                </button>

            </div>
        `;

        usersContainer.appendChild(card);
    });
}

// ====================== LOAD GAMES ======================

function loadGames() {

    gamesContainer.innerHTML = "<p>Loading games...</p>";

    fetch(`${API_BASE}/games`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + adminToken
        }
    })
    .then(response => {

        if (!response.ok) {
            throw new Error("Failed to load games");
        }

        return response.json();
    })
    .then(games => {

        totalGames.innerText = games.length;

        renderGames(games);
    })
    .catch(error => {

        console.error("Games error:", error);

        gamesContainer.innerHTML = "<p>Failed to load games</p>";
    });
}

// ====================== RENDER GAMES ======================

function renderGames(games) {

    gamesContainer.innerHTML = "";

    if (!games || games.length === 0) {

        gamesContainer.innerHTML = "<p>No games found</p>";

        return;
    }

    games.forEach(game => {

        const imagePath = game.imageUrl
            ? `${API_BASE}/images/${game.imageUrl}`
            : "https://placehold.co/300x180?text=No+Image";

        const card = document.createElement("div");

        card.className = "admin-game-card";

        card.innerHTML = `
        
            <div class="admin-game-image-box">

                <img src="${imagePath}" 
                     alt="${game.title}" 
                     class="admin-game-image">

            </div>

            <div class="admin-game-info">

                <h3>${game.title}</h3>

                <p>${game.description}</p>

                <h4>₹${game.price}</h4>

                <button class="delete-game-btn"
                    onclick="deleteGame(${game.id})">

                    Delete Game

                </button>

            </div>
        `;

        gamesContainer.appendChild(card);
    });
}

// ====================== DELETE GAME ======================

function deleteGame(gameId) {

    const confirmDelete = confirm("Delete this game?");

    if (!confirmDelete) {
        return;
    }

    fetch(`${API_BASE}/admin/game/${gameId}`, {
        method: "DELETE",
        headers: {
            "Authorization": "Bearer " + adminToken
        }
    })
    .then(response => response.text())
    .then(message => {

        alert(message);

        loadGames();
    })
    .catch(error => {

        console.error("Delete game error:", error);

        alert("Failed to delete game");
    });
}

// ====================== DELETE USER ======================

function deleteUser(userId) {

    const confirmDelete = confirm("Remove this user?");

    if (!confirmDelete) {
        return;
    }

    fetch(`${API_BASE}/admin/user/${userId}`, {
        method: "DELETE",
        headers: {
            "Authorization": "Bearer " + adminToken
        }
    })
    .then(response => response.text())
    .then(message => {

        alert(message);

        loadUsers();
    })
    .catch(error => {

        console.error("Delete user error:", error);

        alert("Failed to remove user");
    });
}

// ====================== MAKE ADMIN ======================

function makeAdmin(userId) {

    fetch(`${API_BASE}/admin/make-admin/${userId}`, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + adminToken
        }
    })
    .then(response => response.text())
    .then(message => {

        alert(message);

        loadUsers();
    })
    .catch(error => {

        console.error("Make admin error:", error);

        alert("Failed to make admin");
    });
}

// ====================== REMOVE ADMIN ======================

function removeAdmin(userId) {

    fetch(`${API_BASE}/admin/remove-admin/${userId}`, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + adminToken
        }
    })
    .then(response => response.text())
    .then(message => {

        alert(message);

        loadUsers();
    })
    .catch(error => {

        console.error("Remove admin error:", error);

        alert("Failed to remove admin");
    });
}

// ====================== BAN USER ======================

function banUser(userId) {

    fetch(`${API_BASE}/admin/user/ban/${userId}`, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + adminToken
        }
    })
    .then(response => response.text())
    .then(message => {

        alert(message);

        loadUsers();
    })
    .catch(error => {

        console.error("Ban user error:", error);

        alert("Failed to ban user");
    });
}

// ====================== UNBAN USER ======================

function unbanUser(userId) {

    fetch(`${API_BASE}/admin/user/unban/${userId}`, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + adminToken
        }
    })
    .then(response => response.text())
    .then(message => {

        alert(message);

        loadUsers();
    })
    .catch(error => {

        console.error("Unban user error:", error);

        alert("Failed to unban user");
    });
}