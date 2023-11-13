<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <payment-spontaneous-stepper
    last-step-title="Pagamento"
    @on-last-step="onPayment"
    :loading="isLoadingPayment"
  />
</template>

<script>
import PaymentSpontaneousStepper from "../../components/payments/PaymentSpontaneousStepper.vue";
import { setPagoPaUrlNew } from "../../services/api";

export default {
  name: "PagePaymentOnline",
  components: {
    PaymentSpontaneousStepper,
  },
  data() {
    return {
      iuv: null,
      isLoadingPayment: false,
    };
  },
  methods: {
    async onPayment(params) {
      let payload = {
        note: params.notes,
        importo: params.amount,
        nome: params.name,
        cognome: params.surname,
        ragioneSociale: params.businessName,
        codiceFiscalePartitaIVAPagatore: params.taxCode,
        email: params.email,
      };
      let paymentType = params.paymentType;
      this.isLoadingPayment = true;
      try {
        let response = await setPagoPaUrlNew(paymentType, payload);
        let data = response?.data;
        this.iuv = data?.iuv;
        let paymentUrl = data?.paymentUrl;

        if (paymentUrl) {
          window.open(paymentUrl, "_self");
        } else {
          await this.$store.dispatch("setNotificationInfo", {
            body: "Siamo spiacenti, al momento non è possibile completare il pagamento.Ti consigliamo di riprovare più tardi, se il problema persiste contattare l'assistenza",
          });
        }
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile generare il pagamento.",
        });
      }

      this.isLoadingPayment = false;
    },
  },
};
</script>
