"""Export authored pages or import licensed narration. No cloud account/key is embedded in the app."""
import argparse
import hashlib
import json
import pathlib
import re
import shutil

ROOT = pathlib.Path(__file__).resolve().parents[1]

def pages():
    # Read actual Kotlin catalog, so hashes always match the displayed edition.
    source = (ROOT / 'app/src/main/java/com/algokids/data/StoryCatalog.kt').read_text(encoding='utf-8')
    for block in source.split('Story(id=')[1:]:
        story = re.match(r'"([^"]+)"', block).group(1)
        for language, field in [('tr', 'pages'), ('en', 'pagesEn')]:
            match = re.search(r'\b' + field + r'=listOf\((.*?)\),', block, re.S)
            texts = json.loads('[' + match.group(1) + ']')
            for number, text in enumerate(texts, 1):
                yield {'key': f'{language}/{story}_{number}', 'text': text,
                       'text_sha256': hashlib.sha256(text.encode('utf-8')).hexdigest()}

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--export', type=pathlib.Path, help='Write all TR/EN page texts and checksums')
    parser.add_argument('--import-from', type=pathlib.Path, help='Directory with tr/s1_1.mp3, en/s1_1.mp3, etc.')
    parser.add_argument('--voice-credit', help='Provider/actor and commercial licence reference (required for import)')
    parser.add_argument('--partial', action='store_true', help='Allow incomplete pack for preview; missing pages use device voice')
    args = parser.parse_args()
    rows = list(pages())
    if args.export:
        args.export.parent.mkdir(parents=True, exist_ok=True)
        args.export.write_text(json.dumps(rows, ensure_ascii=False, indent=2), encoding='utf-8')
        print(f'Exported {len(rows)} authored pages; no synthesis or network request made.')
    if args.import_from:
        if not args.voice_credit:
            parser.error('--voice-credit is required; use only commercially licensed recordings')
        manifest = {}
        found = []
        for row in rows:
            audio = next((args.import_from / (row['key'] + ext) for ext in ['.mp3', '.wav', '.m4a']
                          if (args.import_from / (row['key'] + ext)).is_file()), None)
            if audio is None:
                continue
            if audio.stat().st_size < 128:
                parser.error(f'Empty or invalid recording: {audio}')
            target = 'narration/' + row['key'] + audio.suffix
            manifest[row['key']] = {'asset': target, 'text_sha256': row['text_sha256'], 'voice_credit': args.voice_credit}
            found.append((audio, target))
        if len(found) != len(rows) and not args.partial:
            parser.error(f'Found {len(found)}/{len(rows)} pages; no files copied. Use --partial only for preview.')
        assets = ROOT / 'app/src/main/assets'
        for audio, target in found:
            destination = assets / target
            destination.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(audio, destination)
        (assets / 'narration').mkdir(parents=True, exist_ok=True)
        (assets / 'narration/index.json').write_text(json.dumps(manifest, ensure_ascii=False, indent=2), encoding='utf-8')
        print(f'Imported {len(found)}/{len(rows)} recordings. Listen to and approve every page before release.')
    if not args.export and not args.import_from:
        parser.error('Choose --export or --import-from')

if __name__ == '__main__':
    main()
