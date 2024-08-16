# Authentication Flow

This document explains the code flow and usage of authentication layer over the application and 
how its managed.

## JWT Service:

This is the class where fundamental authentication methods are defined, which includes:
- **_generateToken()_**: Creation of Token (Takes in claim, make sure to use 'subject' key for 
  adding username - its used after to authenticate the user)
- **_extractClaim()_**: Used to extract data carried in the claim after decoding the token. 
- **_isTokenValid()_**: This methods takes up the claim (uses extractClaim method) and extracts 
  username as well as expiry time to cross check if the token still is valid for that particular user. 
  (we use claim's subject to cross verify username)

> All these methods are used as helper methods/functions in the JWTAuthFilter which acts as a 
> middleware, where all the API hits are first encountered and verified if legit

