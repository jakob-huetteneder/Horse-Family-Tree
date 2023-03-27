import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse, HorseSearch} from '../../dto/horse';
import {Owner} from '../../dto/owner';
import {debounceTime, distinctUntilChanged, map, Subject} from 'rxjs';
import {OwnerService} from '../../service/owner.service';

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  horses: Horse[] = [];
  bannerError: string | null = null;

  sub = new Subject<any>();
  searchParams: HorseSearch = {
    name: undefined,
    description: undefined,
    bornBefore: undefined,
    sex: undefined,
    owner: undefined,
  };

  constructor(
    private service: HorseService,
    private notification: ToastrService,
    private ownerService: OwnerService,

  ) { }

  ngOnInit(): void {
    this.reloadHorses();
    this.sub.pipe(
      debounceTime(300),
      distinctUntilChanged()).subscribe(
      () => {
        this.reloadHorses();
      }
    );
  }

  reloadHorses() {
    console.log('component', this.searchParams.sex);
    this.service.search(this.searchParams)
      .subscribe({
        next: data => {
          this.horses = data;
          console.log('data', data);
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }

  resetHorses() {
    this.searchParams = {
      name: undefined,
      description: undefined,
      bornBefore: undefined,
      sex: undefined,
      owner: undefined,
    };
    this.reloadHorses();
  }

  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  delete(id: number){
    return this.service.delete(id).subscribe({
      next: () => {
        this.notification.success('Horse deleted successfully');
        this.reloadHorses();
      },
      error: err => {
        this.notification.error('Deleting failed', err);
      }
    });
  }
  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  public ownerSuggestions = (input: string) => this.ownerService.searchByName(input, 5)
    .pipe(map<Owner[], string[]>(owners => owners.map(owner => `${owner.firstName} ${owner.lastName}`)));
}
