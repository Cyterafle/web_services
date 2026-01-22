-- Insertion des clients de test
INSERT INTO Customer (id, name, surname, mail, phone, address, verified) VALUES
('11111111-1111-1111-1111-111111111111', 'John', 'Doe', 'john.doe@example.com', '+33612345678', '123 Main Street, Paris', true),
('22222222-2222-2222-2222-222222222222', 'Jane', 'Smith', 'jane.smith@example.com', '+33698765432', '456 Oak Avenue, Lyon', true),
('33333333-3333-3333-3333-333333333333', 'Pierre', 'Martin', 'pierre.martin@example.com', '+33611223344', '789 Rue de la Paix, Marseille', false),
('44444444-4444-4444-4444-444444444444', 'Marie', 'Dupont', 'marie.dupont@example.com', '+33655667788', '321 Boulevard Haussmann, Paris', true);

-- Insertion des polices d'assurance
INSERT INTO Policy (id, policy_number, customer_id, type, coverage_amount, start_date, end_date, active) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'POL-AUTO-001', '11111111-1111-1111-1111-111111111111', 'AUTO', 50000.00, '2024-01-01', '2025-12-31', true),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'POL-HOME-001', '11111111-1111-1111-1111-111111111111', 'HOME', 200000.00, '2024-01-01', '2025-12-31', true),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'POL-HEALTH-001', '22222222-2222-2222-2222-222222222222', 'HEALTH', 100000.00, '2024-01-01', '2025-12-31', true),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'POL-AUTO-002', '33333333-3333-3333-3333-333333333333', 'AUTO', 30000.00, '2024-01-01', '2025-12-31', true),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'POL-TRAVEL-001', '44444444-4444-4444-4444-444444444444', 'TRAVEL', 10000.00, '2024-06-01', '2024-12-31', true),
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'POL-LIFE-001', '44444444-4444-4444-4444-444444444444', 'LIFE', 500000.00, '2024-01-01', '2044-01-01', true),
('00000000-0000-0000-0000-000000000001', 'POL-EXPIRED-001', '22222222-2222-2222-2222-222222222222', 'AUTO', 25000.00, '2022-01-01', '2023-12-31', false);

-- Insertion de réclamations de test
INSERT INTO Claim (id, customer_id, policy_id, policy_number, claim_type, claimed_amount, description, status, fraud_risk_level, approved_amount, created_at, updated_at) VALUES
('12345678-1234-1234-1234-123456789012', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'POL-AUTO-001', 'AUTO', 5000.00, 'Car accident - rear bumper damage', 'SUBMITTED', NULL, NULL, '2024-06-15 10:30:00', '2024-06-15 10:30:00'),
('23456789-2345-2345-2345-234567890123', '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'POL-HEALTH-001', 'HEALTH', 2500.00, 'Medical expenses - surgery', 'APPROVED', 'LOW', 2000.00, '2024-06-10 14:00:00', '2024-06-14 16:30:00'),
('34567890-3456-3456-3456-345678901234', '33333333-3333-3333-3333-333333333333', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'POL-AUTO-002', 'AUTO', 15000.00, 'Total loss - vehicle theft', 'EXPERT_REVIEW', 'MEDIUM', NULL, '2024-06-12 09:00:00', '2024-06-16 11:00:00');

-- Insertion d'historique de réclamations
INSERT INTO Claim_History (id, claim_id, previous_status, new_status, action, details, performed_by, timestamp) VALUES
('aaa11111-1111-1111-1111-111111111111', '12345678-1234-1234-1234-123456789012', NULL, 'SUBMITTED', 'CLAIM_SUBMITTED', 'Claim submitted by customer', 'CUSTOMER', '2024-06-15 10:30:00'),
('bbb22222-2222-2222-2222-222222222222', '23456789-2345-2345-2345-234567890123', NULL, 'SUBMITTED', 'CLAIM_SUBMITTED', 'Claim submitted by customer', 'CUSTOMER', '2024-06-10 14:00:00'),
('bbb22223-2222-2222-2222-222222222222', '23456789-2345-2345-2345-234567890123', 'SUBMITTED', 'IDENTITY_VERIFIED', 'IDENTITY_VERIFICATION', 'Identity verified', 'SYSTEM', '2024-06-10 14:30:00'),
('bbb22224-2222-2222-2222-222222222222', '23456789-2345-2345-2345-234567890123', 'IDENTITY_VERIFIED', 'POLICY_VALIDATED', 'POLICY_VALIDATION', 'Policy validated via SOAP service', 'SOAP_SERVICE', '2024-06-11 09:00:00'),
('bbb22225-2222-2222-2222-222222222222', '23456789-2345-2345-2345-234567890123', 'POLICY_VALIDATED', 'FRAUD_CHECK_LOW', 'FRAUD_DETECTION', 'Low fraud risk detected via gRPC', 'GRPC_SERVICE', '2024-06-12 10:00:00'),
('bbb22226-2222-2222-2222-222222222222', '23456789-2345-2345-2345-234567890123', 'FRAUD_CHECK_LOW', 'APPROVED', 'EXPERT_DECISION', 'Approved by expert', 'EXPERT_JEAN', '2024-06-14 16:30:00'),
('ccc33333-3333-3333-3333-333333333333', '34567890-3456-3456-3456-345678901234', NULL, 'SUBMITTED', 'CLAIM_SUBMITTED', 'Claim submitted by customer', 'CUSTOMER', '2024-06-12 09:00:00'),
('ccc33334-3333-3333-3333-333333333333', '34567890-3456-3456-3456-345678901234', 'SUBMITTED', 'EXPERT_REVIEW', 'ASSIGNED_TO_EXPERT', 'High amount claim assigned to expert', 'SYSTEM', '2024-06-16 11:00:00');
