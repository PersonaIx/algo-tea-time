package org.tea.datastructures.lists.linkedlists;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tea.datastructures.lists.linkedlists.SingleLinkedList;
import org.tea.datastructures.lists.linkedlists.SingleListElement;

import static org.assertj.core.api.Assertions.assertThat;

class SingleLinkedListTest {

    private SingleLinkedList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new SingleLinkedList<>();
    }

    @Test
    void isEmptyOnCreation() {
        assertThat(list.isEmpty()).isTrue();
    }

    @Test
    void addAppendsToEnd() {
        list.add(1);
        list.add(2);
        list.add(3);
        assertThat(list.toList()).containsExactly(1, 2, 3);
    }

    @Test
    void addFirstPrependsToHead() {
        list.addFirst(3);
        list.addFirst(2);
        list.addFirst(1);
        assertThat(list.toList()).containsExactly(1, 2, 3);
    }

    @Test
    void addFirstAndLastMaintainsOrder() {
        list.add(2);
        list.addFirst(1);
        list.add(3);
        assertThat(list.toList()).containsExactly(1, 2, 3);
    }

    @Test
    void removeDeletesExistingElement() {
        list.add(1);
        list.add(2);
        list.add(3);
        list.remove(2);
        assertThat(list.toList()).containsExactly(1, 3);
    }

    @Test
    void removeReturnsFalseForMissingElement() {
        list.add(1);
        assertThat(list.remove(99)).isFalse();
    }

    @Test
    void reverseFlipsList() {
        list.add(1);
        list.add(2);
        list.add(3);
        list.reverse();
        assertThat(list.toList()).containsExactly(3, 2, 1);
    }

    @Test
    void detectsCyclePointingBackToHead() {
        SingleListElement<Integer> n1 = new SingleListElement<>(1);
        SingleListElement<Integer> n2 = new SingleListElement<>(2);
        SingleListElement<Integer> n3 = new SingleListElement<>(3);
        n1.next = n2;
        n2.next = n3;
        n3.next = n1;

        SingleLinkedList<Integer> cycleList = new SingleLinkedList<>();
        cycleList.head = n1;

        assertThat(cycleList.hasCycle()).isTrue();
    }

    @Test
    void detectsCyclePointingBackToMiddle() {
        SingleListElement<Integer> n1 = new SingleListElement<>(1);
        SingleListElement<Integer> n2 = new SingleListElement<>(2);
        SingleListElement<Integer> n3 = new SingleListElement<>(3);
        SingleListElement<Integer> n4 = new SingleListElement<>(4);
        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
        n4.next = n2;   // tail → middle node

        SingleLinkedList<Integer> cycleList = new SingleLinkedList<>();
        cycleList.head = n1;

        assertThat(cycleList.hasCycle()).isTrue();
    }

    @Test
    void detectsSelfReferencingCycle() {
        SingleListElement<Integer> n1 = new SingleListElement<>(1);
        n1.next = n1;   // points to itself

        SingleLinkedList<Integer> cycleList = new SingleLinkedList<>();
        cycleList.head = n1;

        assertThat(cycleList.hasCycle()).isTrue();
    }

    @Test
    void returnsNoCycleForNormalList() {
        list.add(1);
        list.add(2);
        list.add(3);

        assertThat(list.hasCycle()).isFalse();
    }

    @Test
    void returnsNoCycleForSingleElement() {
        list.add(1);

        assertThat(list.hasCycle()).isFalse();
    }

    @Test
    void returnsNoCycleForEmptyList() {
        assertThat(list.hasCycle()).isFalse();
    }
}
