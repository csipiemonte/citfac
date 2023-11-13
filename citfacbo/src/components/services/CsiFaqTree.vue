<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <ul class="csi-tree">
    <li v-for="(node, index) in nodes" :key="index">
      <div class="csi-tree__node" :class="{ active: node.isOpen }">
        <button class="node-btn" @click="toggleNode(node)">
          <span class="arrow"></span>
        </button>
        <input
          class="form-check-input"
          type="radio"
          :value="node"
          name="faq"
          :id="node.id"
          @input="onSelectNode"
        />
        <label class="form-check-label" :for="node.id">
          {{ node.name }}
        </label>
      </div>

      <template v-if="node.children && node.isOpen">
        <div v-if="node.children.length > 0" class="child">
          <CsiFaqTree @onSelect="onSelectChild" :defaultNodes="node.children"></CsiFaqTree>
        </div>
        <div v-else class="px-4 mb-3">
          <small>
            <i>Nessuna sottocategoria</i>
          </small>
        </div>
      </template>
    </li>
  </ul>
</template>

<script>
import { getFaqDetail } from '../../services/api'
import { onShowNotificationToast } from '../../services/business-logic'
export default {
  name: 'CsiFaqTree',
  emits: ['onSelect'],
  props: {
    defaultNodes: { type: Array, required: true, default: () => [] },
    modelValue: { type: Array, default: () => [] }
  },
  data() {
    return {
      selectedFaq: null,
      isLoadingChildren: false,
      nodes: [],
      isOpenNode: false
    }
  },
  mounted() {
    this.nodes = this.defaultNodes.map((node) => {
      node.isOpen = false
      return node
    })
  },
  computed: {},
  methods: {
    async toggleNode(node) {
      node.isOpen = !node.isOpen
      if (!node.children) {
        this.isLoadingChildren = true
        let faq = await this.getFaq(node.id)

        node.children = faq.children
        node.breadcrumb = faq.breadcrumb
        this.isLoadingChildren = false
      }
    },
    onSelectChild(breadcrumb) {
      this.$emit('onSelect', breadcrumb)
    },
    async onSelectNode(event) {
      let node = event.target._value

      if (!node.breadcrumb) {
        let faq = await this.getFaq(node.id)

        node.breadcrumb = faq.breadcrumb
      }

      this.$emit('onSelect', node.breadcrumb)
    },

    async getFaq(id) {
      try {
        let { data: faq } = await getFaqDetail(id)

        return faq
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile recuperare la lista delle FAQ.',
          type: 'error'
        })
      }
    }
  }
}
</script>
