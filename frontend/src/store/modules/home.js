const home = {
    namespaced: true,
    state: {
        selectedYear: null,
    },
    getters: {
        selectedYear: state => state.selectedYear,
    },
    mutations: {
        updateSelectedYear(state, newYear) {
            console.log('Mutation: updateSelectedYear', newYear);
            state.selectedYear = newYear;
        },
    },
};

export default home;