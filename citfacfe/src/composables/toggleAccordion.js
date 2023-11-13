/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

//Funzione che consente di aprire/chiudere  un accordion
/* PARAMS:
* @currentTarget (String) : id della card nascosta (body accordion)
* @currentId (String) : id dell'elemento che scatena l'evento
*/
export const useToggleAccordion = (currentTarget, currentId) => {

    const accordion = document.getElementById(currentTarget);
    const accordionButton = document.getElementById(currentId);

    if (accordionButton.classList.contains("collapsed")) {
        accordionButton.classList.add("showed");
        accordionButton.classList.remove("collapsed");
    } else {
        accordionButton.classList.add("collapsed");
        accordionButton.classList.remove("showed");
    }
    if (accordion.classList.contains("show")) {
        accordion.classList.add("collapse");
        accordion.classList.remove("show");
        accordion.ariaExpanded = "false";
     
    } else {
        accordion.classList.add("show");
        accordion.classList.remove("collapse");
        accordion.ariaExpanded = "true";
    

    }


}
