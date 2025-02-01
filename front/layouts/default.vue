<template>
    <div class="grid min-h-[100dvh] grid-rows-[1fr_auto]">
        <div class="flex">
            <Sidebar/>
            <Toggle/>
            <slot></slot>
        </div>
        <Footer/>
    </div>
</template>
<style>
    body {
        background-color: #121827;
    }
</style>