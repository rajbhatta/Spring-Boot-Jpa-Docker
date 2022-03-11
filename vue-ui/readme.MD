## Case 1. Form posting to API server ##
```js
<template>
  <div class="about">
    <h3>Please submit form1 to test case 1</h3>

<form v-on:submit.prevent="submitForm">
    <div class="field">
      <label class="label">Enter Client Id: </label>
      <div class="control">
        <input type="text" placeholder="Enter Client Id:" v-model="body.clientId"/>
      </div>
    </div>

    <div class="field">
      <label class="label">Enter Quantity: </label>
      <div class="control">
        <input type="number" placeholder="Enter quantity:" v-model="purchaseObj.quantity"/>
      </div>
    </div>

    <div class="field">
      <label class="label">Enter Amount: </label>
      <div class="">
        <input type="text" placeholder="Enter amount:" v-model="purchaseObj.amount"/>
      </div>
    </div>

   <button type="submit" v-on:click="submitForm">Submit</button>
   
</form>
    
  </div>
</template>
<script>
import axios from 'axios';
export default {
  data() {
    return {
      body:{
          clientId: '',
          purchase:[]
      },
      purchaseObj:{
        amount:'',
        quantity:''
      }
    };
  },
  methods: {
    submitForm() {
      this.body.purchase.push(this.purchaseObj)
      axios.post(`${process.env.VUE_APP_API_URL}/api/v1/guusto-service/buy-gift`, this.body)
                 .then((res) => {
                    console.log(res)
                 })
                 .catch((error) => {
                     console.log(error)
                 });
    }
  },
};
</script>
```
