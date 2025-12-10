// Auth Management - Authentication handling
class AuthManager {
    constructor() {
        this.token = localStorage.getItem('token');
        this.user = this.parseJWT(this.token);
    }

    // Decode JWT token
    parseJWT(token) {
        if (!token) return null;
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(
                atob(base64)
                    .split('')
                    .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                    .join('')
            );
            return JSON.parse(jsonPayload);
        } catch (error) {
            console.error('Error parsing JWT:', error);
            return null;
        }
    }

    // Check if token is valid
    isTokenValid() {
        if (!this.token) return false;
        const user = this.parseJWT(this.token);
        if (!user || !user.exp) return false;
        const now = Math.floor(Date.now() / 1000);
        return user.exp > now;
    }

    // Login user
    async login(email, password) {
        try {
            const response = await authAPI.login(email, password);
            if (response.token) {
                localStorage.setItem('token', response.token);
                this.token = response.token;
                this.user = this.parseJWT(response.token);
                return response;
            }
            throw new Error('No token received');
        } catch (error) {
            throw error;
        }
    }

    // Register new user
    async register(email, password, fullName) {
        try {
            const response = await authAPI.register(email, password, fullName);
            if (response.token) {
                localStorage.setItem('token', response.token);
                this.token = response.token;
                this.user = this.parseJWT(response.token);
                return response;
            }
            throw new Error('No token received');
        } catch (error) {
            throw error;
        }
    }

    // Logout user
    logout() {
        localStorage.removeItem('token');
        this.token = null;
        this.user = null;
        window.location.href = '/index.html';
    }

    // Check if user is authenticated
    requireAuth() {
        if (!this.isTokenValid()) {
            this.logout();
            return false;
        }
        return true;
    }

    // Get user information
    getUser() {
        return this.user;
    }

    // Get user email
    getUserEmail() {
        return this.user?.sub || this.user?.email || 'User';
    }
}

// Global auth instance
const auth = new AuthManager();

// Check authentication on protected pages
function checkAuth() {
    if (!auth.requireAuth()) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}
