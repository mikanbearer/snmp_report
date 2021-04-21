

<script>
	import Textfield from '@smui/textfield';
  	import HelperText from '@smui/textfield/helper-text/index';
    
	import LayoutGrid, { Cell } from '@smui/layout-grid';

    export let from;
    export let to;
    export let p;
    export let v;
    export let c;
    export let action_enable;

    let from_invalid = false;
    let to_invalid = false;
    let p_invalid = false;
    let v_invalid = false;
    let c_invalid = false;

    function checkIpAddress(ip) {
        let ip_array = ip.split('.');
        if (ip_array.length == 4) {
            for (let i of ip_array) {
                let i_int = parseInt(i);
                if (i_int < 0 || i_int > 255 || isNaN(i_int)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    $: if (checkIpAddress(from)) {
        from_invalid = false;
    }else {
        from_invalid = true;
    }

    $: if (checkIpAddress(to)) {
        to_invalid = false;
    }else {
        to_invalid = true;
    }
    
    $: if (parseInt(p) > 0 && parseInt(p) <= 65535) {
        p_invalid = false;
    }else {
        p_invalid = true;
    }

    $: if (v == '1' || v== '2' || v== '3') {
        v_invalid = false;
    }else {
        v_invalid = true;
    }

    $: if (c.length > 0) {
        c_invalid = false;
    }else {
        c_invalid = true;
    }

    $: if (!from_invalid && !to_invalid && !p_invalid && !v_invalid && !c_invalid) {
        action_enable = true;
    }else {
        action_enable = false;
    }

</script>




<LayoutGrid>
    <Cell span={6}>
        <Textfield 
            bind:value={from}
            bind:invalid={from_invalid}
            label="From">
            <HelperText validationMsg slot="helper">Invalid ip address!!</HelperText>
        </Textfield>
    </Cell>
    <Cell span={6}>
        <Textfield 
            bind:value={to} 
            bind:invalid={to_invalid}
            label="To">
            <HelperText validationMsg slot="helper">Invalid ip address!!</HelperText>
        </Textfield>
    </Cell>
    <Cell span={5}>
        <Textfield 
            bind:value={c} 
            bind:invalid={c_invalid}
            label="Community">
            <HelperText validationMsg slot="helper">Community can't be empty string!</HelperText>
        </Textfield>
    </Cell>
    <Cell span={4}>
        <Textfield 
            bind:value={p} 
            bind:invalid={p_invalid}
            label="UDP Port">
            <HelperText validationMsg slot="helper">Invalid udp port!</HelperText>
        </Textfield>
    </Cell>
    <Cell span={3}>
        <Textfield 
            bind:value={v}
            bind:invalid={v_invalid}
            label="Version">
            <HelperText validationMsg slot="helper">Invalid snmp version!</HelperText>
        </Textfield>
    </Cell>
</LayoutGrid>