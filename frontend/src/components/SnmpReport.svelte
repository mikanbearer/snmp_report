<script>
	import { fly } from 'svelte/transition';


	import Card, {
		Content,
		PrimaryAction,
  		Actions,
		ActionButtons,
		ActionIcons,
	} from '@smui/card';
	import Button, { Label } from '@smui/button';
	import IconButton, { Icon } from '@smui/icon-button';
	import LayoutGrid, { Cell } from '@smui/layout-grid';
	import LinearProgress from '@smui/linear-progress';

	import InputField from './InputField.svelte';
	import ResultTable from './ResultTable.svelte';
	import Pie from './Pie.svelte'


  
	let start_ip = '192.168.1.1';
	let end_ip = '192.168.1.254';
	let p = '161';
	let v = '2';
	let c = 'public';
	let ip_list = [];

	let pie_data = [];

	let result_list = [];

	let progress = 0;
	let done = true;

	let action_enable = true;

	let paused = false;


	//測試promise是否全數返回
	function testResultPromise() {
		progress = updateProgress();
		if (result_list.length == ip_list.length) {
			for (let a of result_list) {
				if (a.data == null) {
					return false;
				}	
			}
			progress = 1;
			return true;
		}else {
			return false;
		}
	}
	
	function updateProgress() {
		let count = 0; 
		for (let a of result_list) {
			if (a.data != null) {
				count++;
			}	
		}
		return count / ip_list.length;
	}


	//圓餅圖資料
	function createPieData(result_list) {
		let data = {};
		for (let result of result_list) {
			if (data[result.data.type] != null) {
				data[result.data.type]++;
			}else {
				data[result.data.type] = 1
			}
		}
		for (let k of Object.keys(data)) {
			pie_data.push({'label': k, 'value': data[k]});
		}
	}

	//暫停
	function setPause() {
		paused = true;
	}

	function setContinue() {
		paused = false;
	}

	

	/*
	function snmpwalk() {
		reset();
		ip_list = generateIpList(start_ip, end_ip);
		for (let i = 0; i < ip_list.length; i++) {
			//延遲一秒fetch
			setTimeout(n => {
				result_list = [...result_list, {id: n, ip: ip_list[n]}];
				let query = `?ip=${ip_list[n]}&p=${p}&v=${v}&c=${c}`;
				getDeviceInfo('http://127.0.0.1:8080/get' + query).then(r => {
					result_list = result_list.map((item) => {return item.id == n ? {id: n, ip: ip_list[n], data: r} : item});
				});
			}, 1000 * (i + 1), i);
		}
	}
	*/

	//delay用promise
	function sleep(ms)  {
		return new Promise( resolve => { setTimeout(resolve, ms); });
	}

	async function snmpwalk() {
		reset();
		ip_list = generateIpList(start_ip, end_ip);
		for (let i = 0; i < ip_list.length; i++) {
			result_list = [...result_list, {id: i, ip: ip_list[i]}];
			let query = `?ip=${ip_list[i]}&p=${p}&v=${v}&c=${c}`;
			getDeviceInfo('http://127.0.0.1:8080/get' + query).then(r => {
				result_list = result_list.map((item) => {return item.id == i ? {id: i, ip: ip_list[i], data: r} : item});
			});

			//暫停
			while (paused) {
				await sleep(100);
			}

			//延遲一秒
			await sleep(1000);
		}
	}

	//重置用
	function reset() {
		result_list = [];
		pie_data = [];
		done = false;
		paused = false;

	}

	//產生ip list
	function generateIpList(start_ip, end_ip) {
		let start = start_ip.split('.').map(Number);
		let end = end_ip.split('.').map(Number);

		let start_bin = '';
		let end_bin = '';

		let ip_list = [];

		//轉換至二進制
		for (let i = 0; i < 4; i++) {
			let s_part = start[i].toString(2);
			let e_part = end[i].toString(2);
			start_bin += '0'.repeat(8 - s_part.length) + s_part;
			end_bin += '0'.repeat(8 - e_part.length) + e_part;
		}

		//轉換回十進制運算
		for (let i = parseInt(start_bin, 2); i <= parseInt(end_bin, 2); i++) {
			let ip_bin = i.toString(2).match(/^([01]{1,8})([01]{8})([01]{8})([01]{8})$/);
			let ip = [parseInt(ip_bin[1], 2), parseInt(ip_bin[2], 2), parseInt(ip_bin[3], 2), parseInt(ip_bin[4], 2)].join('.');
			ip_list.push(ip);
		}
		
		return ip_list;
	}

	//get
	async function getDeviceInfo(url) {
		const response = await fetch(url, {headers: {
    		Accept: 'application/json'
  		}});
    	return await response.json();
	}

	//移除沒回應的
	$: if (result_list.length > 0) {
		if (testResultPromise()) {
			result_list = result_list.filter((item) => item.data.ip != null);
			createPieData(result_list);
		}
	}
	
	//檢查progress是否完成
	$: if (progress == 1) {
		setTimeout(() => {
			done = true;
		}, 500)
	}
</script>


<div class="card-display" style="max-width: 960px; margin-right: auto; margin-left: auto;">
	
	<LayoutGrid>
		<Cell span={12}>
			<div class="card-container">
				<Card>
				  	<Content>
						  <InputField 
						  	bind:from={start_ip} 
						  	bind:to={end_ip} 
						  	bind:p={p} 
						  	bind:v={v} 
						  	bind:c={c}
							bind:action_enable={action_enable}/>
				  	</Content>
				  	<Actions style="align-self: flex-end;">
						{#if done}
						<Button disabled color="secondary">
							<Label>Pause</Label>
								<i class="material-icons" aria-hidden="true">pause</i>
					  	</Button>
						{:else}
							{#if paused}
							<Button on:click={setContinue} color="secondary">
								<Label>Continue</Label>
								<i class="material-icons" aria-hidden="true">play_arrow</i>
					  		</Button>
							{:else}
						  	<Button on:click={setPause} color="secondary">
								<Label>Pause</Label>
								<i class="material-icons" aria-hidden="true">pause</i>
					  		</Button>
							{/if}
						{/if}
						{#if action_enable}
						<Button on:click={snmpwalk}>
					  		<Label>Scan</Label>
					  			<i class="material-icons" aria-hidden="true">search</i>
						</Button>
						{:else}
						<Button disabled>
							<Label>Scan</Label>
								<i class="material-icons" aria-hidden="true">search</i>
					  	</Button>
						{/if}
				  	</Actions>
  
					<LinearProgress {progress} bind:closed={done} />
				</Card>
		  	</div>
		</Cell>

		{#if pie_data.length > 0 && done}
		<Cell span={12}>
			<div class="card-container" transition:fly="{{ y: 200, duration: 2000 }}">
				<Card>
					<LayoutGrid>
						<Cell span={12}>
							<Pie bind:data={pie_data}/>
						</Cell>
					</LayoutGrid>
				</Card>
		  	</div>
		</Cell>
		{/if}


		{#if result_list.length > 0}
		<Cell span={12} >
			
			<div class="card-container" transition:fly="{{ y: 200, duration: 2000 }}">
				<Card>		
					<ResultTable bind:result_list={result_list}/>
				</Card>
		  	</div>
			
		</Cell>
		{/if}
		
	</LayoutGrid>
 
</div>