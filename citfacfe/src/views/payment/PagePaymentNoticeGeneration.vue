<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <payment-spontaneous-stepper
    last-step-title="Stampa"
    @on-last-step="onPaymentNotice"
    :loading="isLoadingPayment"
  />
</template>

<script>
import PaymentSpontaneousStepper from "../../components/payments/PaymentSpontaneousStepper.vue";
import { SELF_PAYMENT_NOTICE_FEEDBACK } from '../../router/routes';
import { createDebtPositionNotice } from "../../services/api";

export default {
  name: "PagePaymentNoticeGeneration",
  components: { PaymentSpontaneousStepper },
  data() {
    return {
      iuv: null,
      isLoadingPayment: false,
      document: null,
      noticeCode: null,
    };
  },
  methods: {
    async onPaymentNotice(params) {
      this.isLoadingPayment = true;
      let payload = {
        note: params.notes,
        importo: params.amount,
        nome: params.name,
        cognome: params.surname,
        ragioneSociale: params.businessName,
        email: params.email,
      };

      let paymentType = params.paymentType;
      let taxCode = params.taxCode;

      try {
        let { data } = await createDebtPositionNotice(
          taxCode,
          paymentType,
          payload
        );
        let notice = data;

        this.downloadPaymentNotice(notice);
       
        this.$router.replace(SELF_PAYMENT_NOTICE_FEEDBACK);
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile generare l'avviso di pagamento.",
        });
      }

      this.isLoadingPayment = false;
    },
    downloadPaymentNotice(notice) {
    
      const link = document.createElement('a');
      const filename = `avviso-pagamento-${notice.codiceAvviso}`
      const file = notice?.pdf
   
       // E' sempre un PDF
       let url = `data:application/pdf;base64, ${file}`;
      link.target = '_blank';
      link.href = url;
      link.setAttribute('download', filename);

      // Append to html link element page
      document.body.appendChild(link);

      // Start download
      link.click();

      // Clean up and remove the link
      link.parentNode.removeChild(link);
      URL.revokeObjectURL(url);


    },
  },
};
</script>


