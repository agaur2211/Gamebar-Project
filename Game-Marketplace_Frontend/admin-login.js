const API_BASE = "https://gamebar-backend.onrender.com"

const adminForm = document.querySelector(".admin-form");
const loginButton = document.querySelector(".admin-login-btn");

// If already logged in as admin
const existingAdminToken = localStorage.getItem("adminToken");

if (existingAdminToken) {
    window.location.href = "admin-dashboard.html";
}

// Login submit
adminForm.addEventListener("submit", function (event) {
    event.preventDefault();

    adminLogin();
});

// Admin login function
function adminLogin() {

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    if (email === "" || password === "") {
        alert("Please fill all fields");
        return;
    }

    loginButton.innerText = "Logging in...";
    loginButton.disabled = true;

    fetch(`${API_BASE}/admin/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: email,
            password: password
        })
    })
    .then(response => {

        if (!response.ok) {
            throw new Error("Invalid admin credentials");
        }

        return response.text();
    })
    .then(token => {

        // Save admin token
        localStorage.setItem("adminToken", token);

        alert("Admin login successful");

        // Redirect
        window.location.href = "admin-dashboard.html";
    })
    .catch(error => {
        console.error("Admin login error:", error);
        alert(error.message || "Admin login failed");
    })
    .finally(() => {
        loginButton.innerText = "Login as Admin";
        loginButton.disabled = false;
    });
}