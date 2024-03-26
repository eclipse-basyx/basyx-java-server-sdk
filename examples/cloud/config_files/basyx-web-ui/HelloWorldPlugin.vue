<template>
    <v-container fluid class="pa-0">
        <!-- Plugin Title -->
        <v-card class="mb-3">
            <v-card-title>
                <div class="text-subtitle-1">{{ "Hello World Plugin:" }}</div>
            </v-card-title>
        </v-card>
        <!-- Iterate over all SubmodelElements of the HelloWorld-Plugin -->
        <div v-for="SubmodelElement in pluginData" :key="SubmodelElement.idShort" class="mb-3">
            <!-- Display SubmodelElement -->
            <SubmodelElementWrapper :SubmodelElementObject="SubmodelElement" @updateValue="updatePluginValue"></SubmodelElementWrapper>
        </div>
    </v-container>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { useTheme } from 'vuetify';
import { useAASStore } from '@/store/AASDataStore';
import RequestHandling from '@/mixins/RequestHandling'; // Mixin to handle the requests to the AAS
import SubmodelElementHandling from '@/mixins/SubmodelElementHandling'; // Mixin to handle typical SubmodelElement-Actions

import SubmodelElementWrapper from '@/components/UIComponents/SubmodelElementWrapper.vue';

export default defineComponent({
    name: 'PluginJSONArray',
    SemanticID: 'http://hello.world.de/plugin_submodel', // SemanticID of the HelloWorld-Plugin
    props: ['submodelElementData'],
    components: {
        SubmodelElementWrapper,
    },
    mixins: [RequestHandling, SubmodelElementHandling],

    setup() {
        const theme = useTheme()
        const aasStore = useAASStore()

        return {
            theme, // Theme Object
            aasStore, // AASStore Object
        }
    },

    data() {
        return {
            pluginData: {} as any, // Data of the HelloWorld-Plugin
        }
    },

    mounted() {
        // console.log('HelloWorldPlugin mounted');
        this.initializePlugin(); // Initialize the HelloWorld-Plugin when the component is mounted
    },

    computed: {
        // Get the selected Treeview Node (SubmodelElement) from the store
        SelectedNode() {
            return this.aasStore.getSelectedNode;
        },
    },

    methods: {
        // Function to initialize the HelloWorld-Plugin
        initializePlugin() {
            // Check if a Node is selected
            if (Object.keys(this.submodelElementData).length == 0) {
                this.pluginData = {}; // Reset the Plugin Data when no Node is selected
                return;
            }
            let pluginData = { ...this.submodelElementData }; // Get the SubmodelElement from the AAS
            let pluginSubmodelElements = pluginData.submodelElements;
            // add pathes and id's to the SubmodelElements
            this.pluginData = this.preparePluginData(pluginSubmodelElements, this.SelectedNode.path + '/submodelElements');
            // console.log('pluginData: ', this.pluginData)
        },

        // Function to prepare the Plugin Data
        preparePluginData(pluginSubmodelElements: Array<any>, path: string = ''): Array<any> {
            pluginSubmodelElements.forEach((submodelElement: any) => {
                submodelElement.id = this.UUID(); // add a unique id to the SubmodelElement
                submodelElement.path = path + '/' + submodelElement.idShort; // add the path to the SubmodelElement
                // the next Step is not needed for the HelloWorld-Plugin, but it is still displayed as an Example for more complex Situations using SubmodelElementCollections
                if (submodelElement.modelType == 'SubmodelElementCollection') {
                    // Method calls itself to add the pathes and id's to the SubmodelElements in the SubmodelElementCollection
                    submodelElement.children = this.preparePluginData(submodelElement.value, submodelElement.path);
                }
            });
            return pluginSubmodelElements;
        },

        // Function to update the value of a property
        updatePluginValue(submodelElement: any) {
            // find the SubmodelElement in the Plugin Data and replace it with the updated SubmodelElement
            this.pluginData.forEach((element: any, index: number) => {
                if (element.id == submodelElement.id) {
                    this.pluginData[index] = submodelElement;
                }
            });
        },
    },
});
</script>
