/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */


let authorization;
let moonIdentitaJwt;


export function useFetchMoon(input, init) {
  
  let responsePromise;
  if(!authorization) {
    responsePromise= fetch('/api/moon/token')
    .then((response) => response.json())
    .then((data) => {
      authorization = data.token_type + " " +data.access_token;
   
      return Promise.resolve(authorization);
    })
  } else {
    console.log("Alrady resolved authorization")
    responsePromise = Promise.resolve(authorization);
  }
  
  if(!moonIdentitaJwt) {
    responsePromise = responsePromise
      .then((auth) => {
        return fetch('/api/moon/moon-identita', {
          headers: {
              'Authorization': auth
          }
        });
      })
      .then((response) => response.json())
      .then((moonIdentitaResponse) => {
        moonIdentitaJwt = moonIdentitaResponse.idMoonToken;
      
        return Promise.resolve(moonIdentitaJwt);
      });
  } else {
    console.log("Already resolved moonIdentitaJwt")
    responsePromise = Promise.resolve(moonIdentitaJwt);
  }


  responsePromise = responsePromise
    .then((moonIdentitaJwtParam) => {
        
        if(!init){
          init = {};
        }
        if(!init.headers) {
          init.headers = {};
        }
        init.headers['Authorization'] = authorization;
        init.headers['Moon-Identita-JWT'] = moonIdentitaJwtParam;

        let fetchResponse = fetch(input, init);
        fetchResponse.then((response) => {
          //Detect if jwt token Moon-Identita-JWT is expired.
          if(response.status == 403 ){
            let responseText = response.text();
            let isExpired = responseText.includes("ExpiredJWT");
            if(isExpired) {
              console.log("Moon-Identita-JWT is expired! Clean token.");
              moonIdentitaJwt = undefined;
            }
            authorization = undefined;
          }
        })
        .catch((error) => {
          console.log(error);
          moonIdentitaJwt = undefined;
          authorization = undefined;
        });
        return fetchResponse;
        
    });

  return responsePromise;
}

