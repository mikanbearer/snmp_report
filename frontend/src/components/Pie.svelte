<script>
        import FusionCharts from 'fusioncharts';
        import Charts from 'fusioncharts/fusioncharts.charts';
        import FusionTheme from 'fusioncharts/themes/fusioncharts.theme.fusion';
        import SvelteFC, { fcRoot } from 'svelte-fusioncharts';
    
        fcRoot(FusionCharts, Charts, FusionTheme);

        export let data;
    
        let dataSource = {
                'chart': {
                    'caption': 'Device Type',
                    'subCaption': '',
                    'showValues': '1',
                    'showPercentInTooltip': '0',
                    'enableMultiSlicing': '1',
                    'theme': 'fusion'
                },
                'data': data
            },
            chartConfig = {
                type: 'pie2d',
                width: '480',
                height: '320',
                renderAt: 'chart-container',
                dataSource
            };
    </script>
    
    <div id='chart-container' >
        <SvelteFC {...chartConfig} />
    </div>