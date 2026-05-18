
INSERT INTO faqs (question, answer, active, created_at, updated_at) VALUES

('What is SyncBoard?',
 'SyncBoard is a real-time collaboration tool that lets teams create, share, and manage boards, tasks, and notes all in one place.',
 true, NOW(), NOW()),

('How do I create an account?',
 'Click the "Sign Up" button on the homepage, fill in your name, email address, and a strong password, then confirm your email via the link we send you.',
 true, NOW(), NOW()),

('I did not receive my confirmation email. What should I do?',
 'Check your spam or junk folder first. If it is not there, return to the login page and use the "Resend confirmation email" option. Make sure the email address you registered with is correct.',
 true, NOW(), NOW()),

('How do I reset my password?',
 'On the login page, click "Forgot password?", enter your registered email address, and we will send you a reset link valid for 15 minutes.',
 true, NOW(), NOW()),

('Is my data secure?',
 'Yes. All data is encrypted in transit using TLS and passwords are stored using industry-standard bcrypt hashing. We never store plain-text passwords.',
 true, NOW(), NOW()),

('Can I update my profile information?',
 'Yes. After logging in, navigate to your account settings and you can update your name, surname, and profile avatar at any time.',
 true, NOW(), NOW()),

('How do I delete my account?',
 'Go to account settings and select "Delete Account". This action is permanent and cannot be undone. All your data will be removed immediately.',
 true, NOW(), NOW()),

('Is there a mobile app available?',
 'A mobile-friendly web version is available at our URL. Dedicated iOS and Android apps are currently in development.',
 true, NOW(), NOW());