#!/bin/bash

# Expense Tracker API Test Script
# This script demonstrates all the major API endpoints

BASE_URL="http://localhost:8080/api"

echo "=== Expense Tracker API Test ==="
echo

# Test 1: User Registration
echo "1. Testing User Registration..."
curl -X POST "$BASE_URL/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "apitest",
    "email": "apitest@example.com",
    "password": "password123",
    "firstName": "API",
    "lastName": "Test"
  }' \
  -w "\n\n"

# Test 2: User Login
echo "2. Testing User Login..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/signin" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }')

echo "$LOGIN_RESPONSE"
echo

# Extract JWT token
JWT_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "JWT Token: $JWT_TOKEN"
echo

# Test 3: Get Categories
echo "3. Testing Get Categories..."
curl -X GET "$BASE_URL/categories" \
  -H "Content-Type: application/json" \
  -w "\n\n"

# Test 4: Create Transaction
echo "4. Testing Create Transaction..."
curl -X POST "$BASE_URL/transactions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "type": "EXPENSE",
    "amount": 25.99,
    "description": "API Test Transaction",
    "transactionDate": "'$(date +%Y-%m-%d)'",
    "categoryId": 1
  }' \
  -w "\n\n"

# Test 5: Get Transactions
echo "5. Testing Get Transactions..."
curl -X GET "$BASE_URL/transactions?page=0&size=5" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -w "\n\n"

# Test 6: Filter Transactions by Type
echo "6. Testing Filter Transactions by Type (EXPENSE)..."
curl -X GET "$BASE_URL/transactions?type=EXPENSE&page=0&size=3" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -w "\n\n"

# Test 7: Monthly Summary
echo "7. Testing Monthly Summary..."
CURRENT_YEAR=$(date +%Y)
CURRENT_MONTH=$(date +%m)
curl -X GET "$BASE_URL/transactions/summary/monthly?year=$CURRENT_YEAR&month=$((10#$CURRENT_MONTH))" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -w "\n\n"

# Test 8: Annual Summary
echo "8. Testing Annual Summary..."
curl -X GET "$BASE_URL/transactions/summary/annual?year=$CURRENT_YEAR" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -w "\n\n"

# Test 9: Get Current User Info
echo "9. Testing Get Current User Info..."
curl -X GET "$BASE_URL/auth/me" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -w "\n\n"

echo "=== API Test Complete ==="