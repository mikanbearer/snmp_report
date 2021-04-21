<DataTable table$aria-label="People list">
    <Head>
        <Row>
            <Cell>Ip</Cell>
            <Cell>Type</Cell>
            <Cell>Hostname</Cell>
            <Cell>Model</Cell>
            <Cell>Serial</Cell>
            <Cell>Os</Cell>
        </Row>
    </Head>
    <Body>
        {#each result_list as result (result.id)}
            <Row >
                {#if result.data != null}
                    {#if result.data.ip != null}
                        <Cell><p>{result.ip}</p></Cell>
                        <Cell><p>{result.data.type}</p></Cell>
                        <Cell><p>{result.data.hostname}</p></Cell>
                        <Cell><p>{result.data.model}</p></Cell>
                        <Cell><p>{result.data.serial}</p></Cell>
                        <Cell><p>{result.data.os}</p></Cell>
                    {:else}
                        <Cell><p style="color: red;">{result.ip}</p></Cell>
                        <Cell><p style="color: red;">fail</p></Cell>
                    {/if}
                {:else}
                    <Cell><p>{result.ip}</p></Cell>
                    <Cell><p>waiting...</p></Cell>
                {/if}
            </Row>
        {/each}   
    </Body>
</DataTable>
 
<script>
    import DataTable, { Head, Body, Row, Cell } from '@smui/data-table';
    //import { fade } from 'svelte/transition';
    
    export let result_list;
</script>