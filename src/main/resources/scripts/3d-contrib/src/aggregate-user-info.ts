// import * as client from './github-graphql';
// import * as type from './type';
//
// const OTHER_COLOR = '#444444'; // 기본 색상 설정, 언어 색상이 없는 경우 사용
//
// const toNumberContributionLevel = (level: type.ContributionLevel): number => { // 기여 수준을 숫자 값으로 변환하는 함수 정의
//     switch (level) { // 기여 수준에 따른 숫자 반환
//         case 'NONE': return 0;
//         case 'FIRST_QUARTILE': return 1;
//         case 'SECOND_QUARTILE': return 2;
//         case 'THIRD_QUARTILE': return 3;
//         case 'FOURTH_QUARTILE': return 4;
//     }
// };
//
// const compare = (num1: number, num2: number): number => { // 두 숫자 비교하여 정렬에 사용
//     return num2 - num1; // 내림차순 정렬
// };
//
// export const aggregateRepositoryInfo = (
//     response: client.ResponseType // GitHub GraphQL API 응답 타입 매개변수로 받음
// ): type.RepositoryInfo[] => { // RepositoryInfo 배열 반환
//     if (!response.data) { // 응답 데이터가 없는 경우 오류 처리
//         if (response.errors && response.errors.length) { // 응답에 오류 메시지가 있는지 확인
//             throw new Error(response.errors[0].message); // 첫 번째 오류 메시지로 예외 발생
//         } else { // 오류 메시지가 없는 경우
//             throw new Error('JSON\n' + JSON.stringify(response, null, 2)); // 응답 전체를 문자열로 변환하여 예외 발생
//         }
//     }
//
//     const user = response.data.user; // 응답 데이터에서 사용자 정보 추출
//     const repositories = user.repositories.nodes; // 사용자 리포지토리 노드 추출
//
//     // 각 리포지토리별로 정보 집계
//     const repositoryInfos: type.RepositoryInfo[] = repositories.map(repo => {
//         const calendar = repo.contributionsCollection.contributionCalendar.weeks
//             .flatMap(week => week.contributionDays)
//             .map(day => ({
//                 contributionCount: day.contributionCount,
//                 contributionL    evel: toNumberContributionLevel(day.contributionLevel),
//                 date: new Date(day.date),
//             }));
//
//         const contributesLanguage: { [language: string]: type.LangInfo } = {}; // 기여한 언어 정보를 객체로 저장
//         repo.contributionsCollection.commitContributionsByRepository // 각 리포지토리별 커밋 기여 정보 반복 처리
//             .filter((repo) => repo.repository.primaryLanguage) // 주요 언어가 있는 리포지토리만 필터링
//             .forEach((repo) => { // 각 리포지토리에 대해 반복 처리
//                 const language = repo.repository.primaryLanguage?.name || ''; // 언어 이름 가져오기, 없으면 빈 문자열
//                 const color = repo.repository.primaryLanguage?.color || OTHER_COLOR; // 언어 색상 가져오기, 없으면 기본 색상 사용
//                 const contributions = repo.contributions.totalCount; // 리포지토리의 총 기여 횟수 가져오기
//
//                 const info = contributesLanguage[language]; // 해당 언어의 기여 정보 가져오기
//                 if (info) { // 이미 기여 정보가 있는 경우
//                     info.contributions += contributions; // 기존 기여 횟수에 현재 기여 횟수를 추가
//                 } else { // 새로운 언어인 경우
//                     contributesLanguage[language] = { // 새로운 언어 정보 생성하여 저장
//                         language: language, // 언어 이름 저장
//                         color: color, // 언어 색상 저장
//                         contributions: contributions, // 기여 횟수 저장
//                     };
//                 }
//             });
//
//         const languages: Array<type.LangInfo> = Object.values( // 언어 정보 객체를 배열로 변환하여 정렬
//             contributesLanguage
//         ).sort((obj1, obj2) => -compare(obj1.contributions, obj2.contributions)); // 기여 횟수 기준으로 내림차순 정렬
//
//         const totalContributions = calendar.reduce((total, day) => total + day.contributionCount, 0);
//         const totalForkCount = repo.forkCount;
//         const totalStargazerCount = repo.stargazerCount;
//
//         return {
//             name: repo.name,
//             contributionCalendar: calendar,
//             contributesLanguage: languages,
//             totalContributions: totalContributions,
//             totalForkCount: totalForkCount,
//             totalStargazerCount: totalStargazerCount,
//         };
//     });
//
//     return repositoryInfos; // 각 리포지토리별 집계 정보 반환
// };
