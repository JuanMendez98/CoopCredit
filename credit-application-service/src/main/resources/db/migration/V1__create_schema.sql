-- Create Affiliate table
CREATE TABLE affiliates (
    id BIGSERIAL PRIMARY KEY,
    document VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    salary NUMERIC(15, 2) NOT NULL,
    affiliation_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Credit Request table
CREATE TABLE credit_requests (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL REFERENCES affiliates(id) ON DELETE CASCADE,
    amount NUMERIC(15, 2) NOT NULL,
    term INT NOT NULL,
    rate NUMERIC(5, 2) NOT NULL,
    request_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_affiliates_document ON affiliates(document);
CREATE INDEX idx_credit_requests_affiliate_id ON credit_requests(affiliate_id);
CREATE INDEX idx_credit_requests_status ON credit_requests(status);
