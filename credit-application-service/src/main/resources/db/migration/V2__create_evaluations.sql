-- Create Risk Evaluation table
CREATE TABLE risk_evaluations (
    id BIGSERIAL PRIMARY KEY,
    credit_request_id BIGINT NOT NULL UNIQUE REFERENCES credit_requests(id) ON DELETE CASCADE,
    score INT NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    decision VARCHAR(20) NOT NULL,
    reason VARCHAR(500),
    evaluation_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_risk_evaluations_credit_request_id ON risk_evaluations(credit_request_id);
CREATE INDEX idx_risk_evaluations_decision ON risk_evaluations(decision);
