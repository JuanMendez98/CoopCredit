// API Client - Centralized HTTP client
class APIClient {
    constructor(baseURL = 'http://localhost:8080/api') {
        this.baseURL = baseURL;
        this.timeout = 10000;
    }

    // Get token from localStorage
    getToken() {
        return localStorage.getItem('token');
    }

    // Get headers with authentication
    getHeaders() {
        const headers = {
            'Content-Type': 'application/json',
        };
        const token = this.getToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        return headers;
    }

    // Make HTTP request
    async request(method, endpoint, data = null) {
        const url = `${this.baseURL}${endpoint}`;
        const options = {
            method,
            headers: this.getHeaders(),
        };

        if (data && (method === 'POST' || method === 'PUT')) {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await Promise.race([
                fetch(url, options),
                new Promise((_, reject) =>
                    setTimeout(() => reject(new Error('Request timeout')), this.timeout)
                )
            ]);

            if (response.status === 401) {
                localStorage.removeItem('token');
                window.location.href = '/index.html';
                throw new Error('Session expired');
            }

            const contentType = response.headers.get('content-type');
            let body = null;

            if (contentType && contentType.includes('application/json')) {
                body = await response.json();
            } else {
                body = await response.text();
            }

            if (!response.ok) {
                const error = new Error();
                error.status = response.status;
                error.data = body;
                throw error;
            }

            return body;
        } catch (error) {
            console.error(`API Error [${method} ${endpoint}]:`, error);
            throw error;
        }
    }

    // GET request
    get(endpoint) {
        return this.request('GET', endpoint);
    }

    // POST request
    post(endpoint, data) {
        return this.request('POST', endpoint, data);
    }

    // PUT request
    put(endpoint, data) {
        return this.request('PUT', endpoint, data);
    }

    // DELETE request
    delete(endpoint) {
        return this.request('DELETE', endpoint);
    }
}

// Global API client instance
const api = new APIClient();

// ===== Auth API =====
const authAPI = {
    register(email, password, fullName) {
        return api.post('/auth/register', { email, password, fullName });
    },

    login(email, password) {
        return api.post('/auth/login', { email, password });
    }
};

// ===== Affiliates API =====
const affiliatesAPI = {
    getAll() {
        return api.get('/affiliates');
    },

    getById(id) {
        return api.get(`/affiliates/${id}`);
    },

    getByDocument(document) {
        return api.get(`/affiliates/document/${document}`);
    },

    create(data) {
        // Add affiliation date in ISO 8601 format
        const affiliateData = {
            ...data,
            affiliationDate: data.affiliationDate || new Date().toISOString()
        };
        return api.post('/affiliates', affiliateData);
    },

    update(id, data) {
        return api.put(`/affiliates/${id}`, data);
    },

    delete(id) {
        return api.delete(`/affiliates/${id}`);
    }
};

// ===== Credit Requests API =====
const creditRequestsAPI = {
    getAll() {
        return api.get('/credit-requests');
    },

    getById(id) {
        return api.get(`/credit-requests/${id}`);
    },

    getByAffiliateId(affiliateId) {
        return api.get(`/credit-requests/affiliate/${affiliateId}`);
    },

    create(data) {
        return api.post('/credit-requests', data);
    },

    evaluate(id) {
        return api.post(`/credit-requests/${id}/evaluate`, {});
    }
};

// ===== Risk Evaluation API =====
// Risk Service runs on port 8081 (different from Credit Service)
const riskAPIClient = new APIClient('http://localhost:8081/api');
const riskAPI = {
    evaluate(document, amount, term) {
        return riskAPIClient.post('/risk/risk-evaluation', { document, amount, term });
    }
};
