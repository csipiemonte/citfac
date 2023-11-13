/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */



// by convention, composable function names start with "use"
export const  useDownloadBlob = (blob, filename) =>{
  var URL = window.URL || window.webkitURL;
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.target   = '_blank';
  link.href = url;
  link.setAttribute(    //if you just want to preview pdf and dont want download delete this three lines 
    'download',
    filename, 
  );

  // Append to html link element page
  document.body.appendChild(link);

  // Start download
  link.click();

  // Clean up and remove the link
  link.parentNode.removeChild(link);
  URL.revokeObjectURL(url);
}



export const getUrlFromBase64 = (base64, type) =>{
  if(!base64 || !type) return
  return `data:${type};base64, ${base64}`

}