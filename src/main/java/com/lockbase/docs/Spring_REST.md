# Spring Rest

REST - Representational State Transfer (set of rules defined by software architecture style).

- Main Idea is to treat network resources as objects.
- **Client-server Architecture**: means they can interact with each other but should be independent
- **Stateless:** Each HTTP request should contain all info, server should not store any data between 
  requests.
- **Cacheable**
- **Layered System**
- **Unique Identification of Resources**

## Status Codes:

#### 1XX: Informational
#### 2XX: Success
- 200: OK, 
- 201: Created, 
- 204: No content
#### 3XX: Redirection
- 304: Not Modified (used for caching)
> Used for caching, header asks if a (cache) resource is updated, if not then this is sent with 
> empty response.  
#### 4XX: Client Error
- 400: Bad Request (invalid argument or syntax)
- 401: Unauthorized
- 403: Forbidden (Means client does not have permission for request)
#### 5XX: Server Error
- 500: Internal server error (when error occurs at the backend)
- 503: Service temporarily unavailable cause of load
