<template>
<v-container fluid class="pa-0">
    <v-card :key="submodelElementData.idShort">
    <v-card-title>{{ "Grafana visualization:" }}</v-card-title>
    <v-card-subtitle>{{ cardTitle }}</v-card-subtitle>
    <v-card-text>
        <iframe
        :src="iframeSrc"
        width="100%"
        height="100%"
        frameborder="0"
        ref="iframe"
        ></iframe>
    </v-card-text>
    </v-card>
</v-container>
</template>
<script lang="ts">
import { defineComponent } from "vue";
import { useTheme } from "vuetify";
import { useAASStore } from "@/store/AASDataStore";

export default defineComponent({
name: "TimeSeriesDataPlugin",
SemanticID:
    "https://digital-factory.tno.nl/plugins/streaming-data-visualization", // SemanticID of the HelloWorld-Plugin
props: ["submodelElementData"],

setup() {
    const theme = useTheme();
    const aasStore = useAASStore();

    return {
    theme, // Theme Object
    aasStore, // AASStore Object
    };
},
data() {
    return {
    iframeSrc: "",
    };
},
mounted() {
    this.initChart();
},

computed: {
    // get selected AAS from Store
    SelectedAAS() {
    return this.aasStore.getSelectedAAS;
    },

    // Get the selected Treeview Node (SubmodelElement) from the store
    SelectedNode() {
    return this.aasStore.getSelectedNode;
    },

    // Check if the current Theme is dark
    isDark() {
    return this.theme.global.current.value.dark;
    },
    cardTitle() {
    return this.submodelElementData.idShort || "Default Title";
    },
},
watch: {
    // watch for changes in the vuetify theme and update the chart options
    isDark() {
    this.applyTheme();
    },
},
methods: {
    initChart() {
    // Check if a Node is selected
    if (Object.keys(this.submodelElementData).length == 0) {
        return;
    }
    let chartData = this.submodelElementData.value; // parse the value of the SubmodelElement
    if (this.isDark) {
        chartData += "&theme=dark";
    } else {
        chartData += "&theme=light";
    }
    const iframe = this.$refs.iframe as HTMLIFrameElement;

    iframe.src = chartData;
    },

    applyTheme() {
    if (this.isDark) {
        let chartData = this.submodelElementData.value;
        const iframe = this.$refs.iframe as HTMLIFrameElement;
        iframe.src = chartData + "&theme=dark";
    } else {
        let chartData = this.submodelElementData.value;
        const iframe = this.$refs.iframe as HTMLIFrameElement;
        iframe.src = chartData + "&theme=light";
    }
    },
},
});
</script>  