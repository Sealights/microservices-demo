{
  "consumer": {
    "name": "ConsumerService"
  },
  "provider": {
    "name": "ProviderService"
  },
  "interactions": [
    {
      "description": "A request to the index",
      "request": {
        "method": "GET",
        "path": "/"
      },
      "response": {
        "status": 200
      }
    },
    {
      "description": "Setting currency",
      "request": {
        "method": "POST",
        "path": "/setCurrency",
        "body": {
          "currency_code": "EUR"
        }
      },
      "response": {
        "status": 200
      }
    },
    {
      "description": "Browsing a product",
      "request": {
        "method": "GET",
        "path": "/product/0PUK6V6EV0"
      },
      "response": {
        "status": 200
      }
    },
    {
      "description": "Adding to cart",
      "request": {
        "method": "POST",
        "path": "/cart",
        "body": {
          "product_id": "0PUK6V6EV0",
          "quantity": 1
        }
      },
      "response": {
        "status": 200
      }
    }
  ],
  "metadata": {
    "pactSpecification": {
      "version": "2.0.0"
    }
  }
}
