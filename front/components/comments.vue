<script setup lang="ts">
import { ref, onMounted, Ref } from 'vue';
type Comment = {
    id: number;
    text: string;
    author: string;
    date: string;
    id_prev: number | null;
};
interface Props {
    comments: Comment[];
}

const props = defineProps<Props>();

const commentsTree: Ref<Comment[][]> = ref([]);

/*let comments:Comment[] = [];
function mapComments(id:number | null):Comment[] {
    if (id === null) {
        return comments;
    }
    let element = props.comments.find(comment => comment.id === id);
    comments = [...comments, element];
    return mapComments(element.id_prev);
}*/

function mapComments(id: number | null): Comment[] {
    if (id === null) {
        return [];
    }
    const element = props.comments.find(comment => comment.id === id);
    if (!element) {
        return [];
    }
    return [element, ...mapComments(element.id_prev)];
}

onMounted(() => {
    props.comments.forEach((comment) => {
        if (comment.id_prev === null) {
            commentsTree.value.push(mapComments(comment.id));
            //comments = [];
        }
    });
});
</script>

<template>
    <div>
        <h1>Comments</h1>
        <div v-for="(chain, index) in commentsTree" :key="index">
            <div v-for="(comment, index) in chain" :key="index">
                <p><strong>{{ comment.author }}</strong> on {{ comment.date }}</p>
                <p>{{ comment.text }}</p>
            </div>
        </div>
    </div>
</template>
