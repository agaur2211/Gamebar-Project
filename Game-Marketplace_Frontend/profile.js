const API_BASE = "https://gamebar-backend.onrender.com"
const token = localStorage.getItem("userToken");

const nameInput = document.getElementById("name");
const emailInput = document.getElementById("email");
const whatsappInput = document.getElementById("whatsapp");

const updateBtn = document.getElementById("updateBtn");
const logoutBtn = document.getElementById("logoutBtn");

// Protect page
if (!token) {
    alert("Please login first");
    window.location.href = "login.html";
}

// Load profile on page open
window.addEventListener("load", function () {
    loadProfile();
});

// Logout
if (logoutBtn) {
    logoutBtn.addEventListener("click", function () {
        localStorage.removeItem("userToken");
        localStorage.removeItem("userEmail");
        window.location.href = "login.html";
    });
}

// Update profile
if (updateBtn) {
    updateBtn.addEventListener("click", function () {
        updateProfile();
    });
}

// Load profile data
function loadProfile() {

    fetch(`${API_BASE}/users/profile`, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Failed to load profile");
        }

        return response.json();
    })
    .then(user => {

        nameInput.value = user.name || "";
        emailInput.value = user.email || "";
        whatsappInput.value = user.whatsappNumber || "";

    })
    .catch(error => {
        console.error("Profile load error:", error);
        alert("Failed to load profile");
    });
}

// Update profile data
function updateProfile() {

    const updatedName = nameInput.value.trim();
    const updatedWhatsapp = whatsappInput.value.trim();

    if (updatedName === "" || updatedWhatsapp === "") {
        alert("Please fill all fields");
        return;
    }

    updateBtn.innerText = "Updating...";
    updateBtn.disabled = true;

    fetch(`${API_BASE}/users/update`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify({
            name: updatedName,
            whatsappNumber: updatedWhatsapp
        })
    })
    .then(response => {

        if (!response.ok) {
            throw new Error("Failed to update profile");
        }

        return response.text();
    })
    .then(message => {

        alert(message || "Profile updated successfully");

    })
    .catch(error => {
        console.error("Update error:", error);
        alert("Failed to update profile");
    })
    .finally(() => {
        updateBtn.innerText = "Update Profile";
        updateBtn.disabled = false;
    });
}

const addGameBtn = document.getElementById("addGameBtn");
const adminPortalBtn = document.getElementById("adminPortalBtn");

// Redirect to add game page
if (addGameBtn) {

    addGameBtn.addEventListener("click", function () {

        window.location.href = "addgame.html";
    });
}

// Redirect to admin login page
if (adminPortalBtn) {

    adminPortalBtn.addEventListener("click", function () {

        const confirmAdmin = confirm(
            "Are you an admin? You will be redirected to admin login."
        );

        if (confirmAdmin) {

            window.location.href = "admin-login.html";
        }
    });
}