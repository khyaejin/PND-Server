import { mkdirSync, writeFileSync } from 'fs';
import * as os from 'os';

let OUTPUT_FOLDER: string;

if (os.platform() === 'win32') {
    // Windows path
    OUTPUT_FOLDER = './src/main/resources/profile-3d-contrib';
} else if (os.platform() === 'darwin') {
    // macOS path
    OUTPUT_FOLDER = '/Users/gimhyejin/Library/CloudStorage/OneDrive-한성대학교/문서/Projects/PND-Server/src/main/resources/profile-3d-contrib';
} else {
    // Deploy path for EC2 (Linux)
    OUTPUT_FOLDER = '/PND-Server/src/main/resources/profile-3d-contrib';
}

export { OUTPUT_FOLDER };

// 파일 저장 위치 설정
export const writeFile = (fileName: string, content: string): void => {
    mkdirSync(OUTPUT_FOLDER, { recursive: true });
    writeFileSync(`${OUTPUT_FOLDER}/${fileName}`, content);
};
