import {Component, Input} from '@angular/core';
import {Horse} from '../../../../dto/horse';
import {Sex} from '../../../../dto/sex';
import {HorseService} from '../../../../service/horse.service';
import {OwnerService} from '../../../../service/owner.service';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-horse-family-tree-node',
  templateUrl: './horse-family-tree-node.component.html',
  styleUrls: ['./horse-family-tree-node.component.scss']
})
export class HorseFamilyTreeNodeComponent {
  @Input() generations = 0;
  @Input() horse: Horse = {
    name: '',
    description: '',
    dateOfBirth: new Date(),
    sex: Sex.female,
  };
  hide = false;

  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }
  hideParents(): void {
    this.hide = !this.hide;
  }
  delete(id: number){
    return this.service.delete(id).subscribe({
      next: () => {
        this.notification.success('Horse deleted successfully');
        this.router.navigate(['horses']);
      },
      error: err => {
        this.notification.error('Deleting failed', err);
      }
    });
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }
}
