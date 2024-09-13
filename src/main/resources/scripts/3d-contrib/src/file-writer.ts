import { mkdirSync, writeFileSync } from 'fs';

export const OUTPUT_FOLDER = './src/main/resources/profile-3d-contrib';
// 파일 저장 위치 설정
export const writeFile = (fileName: string, content: string): void => {
    mkdirSync(OUTPUT_FOLDER, { recursive: true });
    writeFileSync(`${OUTPUT_FOLDER}/${fileName}`, content);
};
